#ifndef __PROXY_H__
#define __PROXY_H__
#endif

#ifndef __CSAPP_H__
#include "csapp.h"
#endif

#define MAX_MSG_SIZE 2048

struct thread_s {
  int connfd;
  struct sockaddr_in clientaddr;
};

/* Workload function */
void doit(int fd, struct sockaddr_in clientaddr);

/* Cache handling */
char *cached(char *uri);
void cache_insert(char *uri, char *msg);
void init_cache(); 

/* Logging */
void concat(char *dest, char *src, int offset);
void write_log_entry(char *logstring);
void clear_log();

/* Thread safety*/
void *thread(void *sp);
int Open_clientfd_ts(char *hostname, int portno); /* Uses lock-and-copy technique for thread-safety*/

/* Signal handler for dropped connections*/
void sigpipe_handler(int sig);
        
/* Helper functions */
int parse_uri(char *uri, char *target_addr, char *path, int  *port);
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr, char *uri, int size);
int client_rqst(char *method);
void handle_client(int fd, char *msgbufbuf, char *uri, struct sockaddr_in clientaddr);
void forward(int connfd, char *uri, char *host, int *port, char *buf, struct sockaddr_in clientaddr);
void receive(char *uri, rio_t rio, char *buf, int connfd, struct sockaddr_in clientaddr);
void send_msg(int clientfd, char *msg, int n);
void init_proxy();
void clienterror(int fd, char *cause, char *errnum, 
		 char *shortmsg, char *longmsg);
