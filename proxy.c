/*
 *  Implements a concurrent thread-safe HTTP proxy.
 *  It safetly handles SIGPIPE signals from dropped connections.
 */ 

#include "proxy.h"
#include "csapp.h"
#include "cache.h"


/* Global variables */
static sem_t mutex;

/* 
 * main - Main routine for the proxy program 
 */
int main(int argc, char **argv)
{
    int listenfd, port;
    struct thread_s *sp;
    unsigned int clientlen = sizeof(struct sockaddr_in);
    pthread_t tid;
    
    /* Check command line args */
    if (argc != 2) {
	fprintf(stderr, "usage: %s <port>\n", argv[0]);
	exit(1);
    }
    port = atoi(argv[1]);
    init_proxy();
    listenfd = Open_listenfd(port);

    while (1) {

      sp = Malloc(sizeof(struct thread_s));
      sp->connfd = Accept(listenfd, (SA *)&sp->clientaddr, &clientlen);
      Pthread_create(&tid, NULL, thread, (void*)sp);
    }

}

/*
 * Each thread runs this function
 */
void *thread(void *sp){
  struct thread_s *ts = (struct thread_s *)sp;
  int connfd = ts->connfd;
  struct sockaddr_in clientaddr = ts->clientaddr;
  Pthread_detach(pthread_self());
  Free(sp);
  doit(connfd, clientaddr); /* Handle client connection */
  Close(connfd);
  return NULL;
}


/*
 * doit - handle one HTTP request/response transaction per thread.
 */ 
void doit(int fd, struct sockaddr_in clientaddr) 
{
    char buf[MAXLINE], method[MAXLINE], uri[MAXLINE], version[MAXLINE];
    rio_t rio;

    /* Read request line and headers */
    Rio_readinitb(&rio, fd);
    Rio_readlineb(&rio, buf, MAXLINE);                   
    sscanf(buf, "%s %s %s", method, uri, version);

    if (client_rqst(method))
      handle_client(fd, buf, uri, clientaddr);

    else {
      clienterror(fd, method, "501", "Not Implemented",
                "Tiny does not implement this method");
      return;
    }


}

/*
 *  Handle the HTTP request
 */
void handle_client(int connfd, char *msg, char *uri, struct sockaddr_in clientaddr){

  char host[MAXLINE], path[MAXLINE];
  char *rsp;
  int port;
  if (parse_uri(uri, host, path, &port) == -1){
    clienterror(connfd, uri, "501", "Not Implemented",
                "There was a problem with your URI.");
    return;
  }

  if((rsp = cached(uri)) == NULL) 
    forward(connfd, uri, host, &port, msg, clientaddr); 

  else 
    send_msg(connfd, rsp, strlen(rsp)); 
}

/*
 * Forwards HTTP request to host and receives its response.
 */
void forward(int connfd, char *uri, char *host, int *port, char *msg, struct sockaddr_in clientaddr){

  int clientfd;
  char buf[MAXLINE];
  rio_t rio;
  clientfd = Open_clientfd_ts(host, *port);
  Rio_readinitb(&rio, clientfd);
  send_msg(clientfd, msg, strlen(msg));       
  receive(uri, rio, buf, connfd, clientaddr); 
  Close(clientfd);
}

void send_msg(int conn, char *msg, int n){
 Rio_writen(conn, msg, n); 
}

void receive(char *uri, rio_t rio, char *msg, int connfd, struct sockaddr_in clientaddr){

  int n, size = 0;
  char *logstring = NULL;
  while ((n = Rio_readnb(&rio, msg, MAXLINE)) != 0){
    size = n;
    printf("bytes %d:\n", n);
        
  }
     P(&mutex);                     /* lock around cache insertion */
     cache_insert(uri, msg);       /*cache server response*/
     V(&mutex);
     format_log_entry(logstring, &clientaddr, uri, size);
     P(&mutex);                    /* lock around file logging */
     write_log_entry(logstring);   /*write to proxy.log*/
     V(&mutex);
     printf("fd : %d\n", connfd);
     send_msg(connfd, msg, size);  /*relay to client*/
  
}


/****** Helper functions ******/
int client_rqst(char *method){

if (!strcasecmp(method, "GET"))                      
  return 1;

 return 0;
}

/* Looks for a cached copy of the object */
char *cached(char *uri){

  char *rsp;
  rsp = contains(uri);
  return rsp;
}

void cache_insert(char *uri, char *payload){

  insert_node(uri, payload); 
}

void concat(char *dest, char *src, int offset){
  memcpy(&dest[offset], src, strlen(src));
}

void write_log_entry(char *logstring){

  FILE *fp;
  if ((fp = Fopen("proxy.log" , "a")) == NULL)
    return;
  
  strcat(logstring, "\n");
  Fwrite(logstring, 1, strlen(logstring), fp);
  Fclose(fp);
  
}

void clear_log(){
  
  FILE *fp;
  if ((fp = Fopen("proxy.log" , "w")) == NULL)
    return;
  Fclose(fp);
}

/* Initializes the proxy */
void init_proxy(){
  
    Signal(SIGPIPE, sigpipe_handler);
    init_cache(); /* Initialize cache on the heap */
    clear_log();  /* Creates a fresh log */
    Sem_init(&mutex, 0, 1);

}

/*
 *   open connection to server at <hostname, port> 
 *   and return a socket descriptor ready for reading and writing.
 *   'gethostbyname' is thread-unsafe since it returns a static variable
 *   We use the lock and copy method to make it thread-safe.
 *   Returns -1 and sets errno on Unix error. 
 *   Returns -2 and sets h_errno on DNS (gethostbyname) error.
 */
int Open_clientfd_ts(char *hostname, int port) 
{
    int clientfd;
    struct hostent *hp;
    struct hostent *cpy = NULL;
    struct sockaddr_in serveraddr;

    if ((clientfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	return -1; /* check errno for cause of error */
    
    P(&mutex); /* Lock mutex */
    if ((hp = gethostbyname(hostname)) == NULL)
	return -2; /* check h_errno for cause of error */
    memcpy(cpy, hp, sizeof(struct hostent));
    V(&mutex);
    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *)cpy->h_addr_list[0], 
	  (char *)&serveraddr.sin_addr.s_addr, cpy->h_length);
    serveraddr.sin_port = htons(port);

    /* Establish a connection with the server */
    if (connect(clientfd, (SA *) &serveraddr, sizeof(serveraddr)) < 0)
	return -1;
    return clientfd;
}


/*
 * Writing to a dropped connection cause a SIGPIPE to be delivered
 * whose default action is to terminate the process.  Here we safetly
 * handle receiving a SIGPIPE.
 */
void sigpipe_handler(int sig)
{
  printf("Writing to a lost connection.\n Safetly handling SIGPIPE\n");
}



/* 
 * Given a URI from an HTTP proxy GET request (i.e., a URL), extract
 * the host name, path name, and port.  The memory for hostname and
 * pathname must already be allocated and should be at least MAXLINE
 * bytes. Return -1 if there are any problems.
 */
int parse_uri(char *uri, char *hostname, char *pathname, int *port)
{
    char *hostbegin;
    char *hostend;


    char *pathbegin;
    int len;

    if (strncasecmp(uri, "http://", 7) != 0) {
	hostname[0] = '\0';
	return -1;
    }
       
    /* Extract the host name */
    hostbegin = uri + 7;
    hostend = strpbrk(hostbegin, " :/\r\n\0");
    len = hostend - hostbegin;
    strncpy(hostname, hostbegin, len);
    hostname[len] = '\0';
    
    /* Extract the port number */
    *port = 80; /* default */
    if (*hostend == ':')
	*port = atoi(hostend + 1);
    
    /* Extract the path */
    pathbegin = strchr(hostbegin, '/');
    if (pathbegin == NULL) {
	pathname[0] = '\0';
    }
    else {
	pathbegin++;
	strcpy(pathname, pathbegin);
    }

    return 0;
}



/*
 * format_log_entry - Create a formatted log entry in logstring. 
 * 
 * The inputs are the socket address of the requesting client
 * (sockaddr), the URI from the request (uri), and the size in bytes
 * of the response from the server (size).
 */
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr, 
		      char *uri, int size)
{
    time_t now;
    char time_str[MAXLINE];
    unsigned long host;
    unsigned char a, b, c, d;

    /* Get a formatted time string */
    now = time(NULL);
    strftime(time_str, MAXLINE, "%a %d %b %Y %H:%M:%S %Z", localtime(&now));

    /* 
     * Convert the IP address in network byte order to dotted decimal
     * form. Note that we could have used inet_ntoa, but chose not to
     * because inet_ntoa is a Class 3 thread unsafe function that
     * returns a pointer to a static variable (Ch 13, CS:APP).
     */
    host = ntohl(sockaddr->sin_addr.s_addr);
    a = host >> 24;
    b = (host >> 16) & 0xff;
    c = (host >> 8) & 0xff;
    d = host & 0xff;


    /* Return the formatted log entry string */
    sprintf(logstring, "%s: %d.%d.%d.%d %s %d", time_str, a, b, c, d, uri, size);
}


/*
 * clienterror - returns an error message to the client
 */
void clienterror(int fd, char *cause, char *errnum, 
		 char *shortmsg, char *longmsg) 
{
    char buf[MAXLINE], body[MAXBUF];

    /* Build the HTTP response body */
    sprintf(body, "<html><title>Tiny Error</title>");
    sprintf(body, "%s<body bgcolor=""ffffff"">\r\n", body);
    sprintf(body, "%s%s: %s\r\n", body, errnum, shortmsg);
    sprintf(body, "%s<p>%s: %s\r\n", body, longmsg, cause);
    sprintf(body, "%s<hr><em>The Tiny Web server</em>\r\n", body);

    /* Print the HTTP response */
    sprintf(buf, "HTTP/1.0 %s %s\r\n", errnum, shortmsg);
    Rio_writen(fd, buf, strlen(buf));
    sprintf(buf, "Content-type: text/html\r\n");
    Rio_writen(fd, buf, strlen(buf));
    sprintf(buf, "Content-length: %d\r\n\r\n", (int)strlen(body));
    Rio_writen(fd, buf, strlen(buf));
    Rio_writen(fd, body, strlen(body));
}
