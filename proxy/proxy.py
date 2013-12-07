import socket, sys, re, os, SocketServer, threading, logging, logging.config, datetime
import email.utils as eut
from urlparse import urlparse
import pdb
# List of all accepted URI headers. Connection is omitted because it is always replaced with 'close' anyway.
header_fields = ['Accept', 'Accept-Charset', 'Accept-Encoding', 'Accept-Language', 'Accept-Datetime',
'Authorization', 'Cache-Control', 'Cookie', 'Content-Length', 'Content-MD5', 'Content-Type',
'Date', 'Expect', 'From', 'Host', 'If-Match', 'If-Modified-Since', 'If-None-Match', 'If-Range', 'If-Unmodified-Since',
'Max-Forwards', 'Pragma', 'Proxy-Authorization', 'Range', 'Referrer', 'TE', 'Upgrade', 'User-Agent', 'Via', 'Warning']    


class TCPRequestHandler(SocketServer.BaseRequestHandler):
    

    """
    Entry point for multi-processing. Uses the inherited class functions to obtain the client socket.
    Requests from client must come in the form of a valid URI.
    @see: get_uri(cli_request) for more information on URI formatting.
     """
    def handle(self):
        pdb.set_trace()
        msg = self.request.recv(1024)
        rqst = self.parse_rqst(msg)
        if 'error' not in rqst:
            
            # Attempt to load from cache. If the file doesn't exist or is corrupt, throw an IOError exception
            # and reload from the remote server.
            try:
                
                f = open("cache/" + rqst['host'] + rqst['url'].replace("/", "-"), 'rb')         
                data = f.readlines()
                f.close()
                if self.valid_cache(data):
                    # Data still good.
                    logging.info("Valid file exists in cache. Sending to client.")
                    for i in range(0, len(data)):
                        self.request.send(data[i])
                else:
                    logging.info("Invalid file exists in cache. Requesting new copy.")
                    raise IOError
                
            except IOError:
    
                try:
                    # Request is valid and not cached. Open a connection and retrieve the web page.
                    s = self.get_uri(rqst)
                    outbound = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    print 'rqst host:'
                    print rqst['host']
                    logging.info("Client %s requested host %s" % (self.client_address, rqst['host']))
                    outbound.connect((rqst['host'], 80))
    
                    # Write the socket buffer to a temporary file, and then read those lines into a buffer.
                    try:
                        fileobj = outbound.makefile('r', 0)
#                        print s
                        fileobj.write(s)
                       
                        buffer = fileobj.readlines()
 #                       print buffer
                        # Cache the result.                        
                        tmpFile = open("cache/" + rqst['host'] + rqst['url'].replace("/", "-"), 'wb')
                        
                        # Send the data to the requesting client.
                        for i in range(0, len(buffer)):
                            tmpFile.write(buffer[i])
                            self.request.send(buffer[i])
    
                        tmpFile.close()
                    except socket.timeout, e:
                        logging.error(str(e))
                        
                except Exception, e:
                    self.request.send(self.get_err("Error 400: Bad Request"))
                    logging.error(str(e))
                    
            # Close the client socket
            self.request.close() 
        else:
            logging.error("Client %s error: %s" % (str(self.client_address), rqst['error']))
            self.request.send(self.get_err(rqst['error']))
            self.request.close()


    """
    Takes a properly formatted HTTP request and returns the formatted URI request.
    Refer to RFC 1945 section 5.4.2 for details on how URI requests are formatted.
    @param request: Hash of a valid HTTP GET request.
    @return: A (hopefully) valid URI
    """
    def get_uri(self, request):

        hdrs = [(_h.split(':')[0].strip(), _h.split(':')[1].strip()) for _h in request['headers'].splitlines() if _h.split(':')[0] in header_fields]
        hdrs.append(('Connection', 'close'))
        
        rslt = "".join("%s: %s\r\n" % h for h in hdrs)
            
        rslt += "\r\n"
        
        # Form the URI request in its entirety. 
        if len(request['url']) > 1:
            uri = "GET http://{0} {1}\r\n{2}".format(request['host'] + request['url'], request['version'], rslt)
        else:
            uri = "GET / {1}\r\n{2}".format(request['host'], request['version'], rslt)
        return uri
    
        
    """
    Given a message received from a connected client, determine if it is a valid HTTP request.
    @param message: Client HTTP request. 
    @return: Hash of valid URI fields.
    @return: None upon encountering an error.
    """
    def parse_rqst(self, message):

        # Breakdown of regex:        GET      AbsoluteURL (per rfc)        HTTP version            Everything else.
        regex = re.compile(r"""(?P<method>\w+) (?P<url>.*) (?P<version>HTTP/\d\.\d)\r*\n*(?P<headers>.*)\r*\n""", re.DOTALL)
        match = regex.match(message)
        if match is not None:
            mthd = match.group('method')
            url = match.group('url')
            vrsn = match.group('version')
            hdrs = match.group('headers')
            
            if 'GET' in mthd:
                return {'method':mthd, 'url':urlparse(url)[2], 'version':vrsn, 'headers': hdrs, 'host': urlparse(url)[1]}
            else:
                return {'error': "Error 501: Not Implemented"}
        else:
            return {'error': "Error 400: Bad Request"}

        
    """
    Builds a very basic webpage string in the event of an error in between the client and proxy server. 
    @param errmsg: String containing an HTTP error message.
    @return: Webpage readable string. 
    """         
    def get_err(self, errmsg):
       return """<html><head></head><body><font size="20"><b>{0}</b></font></body></html>""".format(errmsg)


    """
    Determines if the current file is still within the appropriate age to be considered valid.
    @param data: data buffer from a cached file 
    @return: True if cache file is still valid, false otherwise.
    """   
    def valid_cache(self, data):
        # List of good words and bad words. Bad words imply that the file must always be refreshed.
        bad = ['no-cache', 'no-store', 'must-revalidate', 'proxy-revalidate']
        good = ['max-age', 's-maxage']
        valid_cache = [(d) for d in data if 'Cache-Control' in d]
        valid_cache = '\n'.join(valid_cache)

        for s in bad or not valid_cache:
            if s in valid_cache:
                return False
        
        # Attempt to retrieve max-age from the cache-control field. 
        # Throws an exception if there is strange formatting (eg, not a number).
        max_age = None
        for g in good:
            if g in valid_cache:
                try:
                    max_age = int(valid_cache.split('=')[1])
                except ValueError as e:
                    logging.error(str(e))
                    return False
        if not max_age:
            return False
        
        cached_date = [(datetime.datetime(*eut.parsedate(d[5:])[:6])) for d in data if 'Date:' in d]
        current_date = datetime.datetime.now()
        age = (current_date - cached_date[0]).total_seconds()

        if age <= max_age:
            return True  # Valid cache
        else:
            return False

"""
Overloaded constructor for a threadedTCPServer object. Template acquired via python.org's docs.
"""
class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    pass

if __name__ == '__main__':
    
    if len(sys.argv) <= 1 or len(sys.argv) > 2:
        print 'Usage : "python pyproxy.py <server_port>"'
        sys.exit(0)

    logging.basicConfig(level=logging.INFO)
 
    if not os.path.exists('./cache'):
        os.makedirs('./cache')

    # Clear cache prior to run (for testing/grading purposes.)
    filelist = [ f for f in os.listdir("./cache")]
    # for f in filelist:
    #     os.remove("./cache/" + f)
    
    host = ''
    port = int(sys.argv[1])
    
    #Start serving requests...
    server = ThreadedTCPServer((host, port), TCPRequestHandler)
    ip, port = server.server_address
    
    # Start a thread with the server -- that thread will then start one more thread for each request
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.daemon = True
    server_thread.start()
    while True:
        continue
    
 


