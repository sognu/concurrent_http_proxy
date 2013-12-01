import socket
import rsa
import pickle
import sys, getopt, logging
from rsa.bigfile import *
from pyDes import *

def start(host, port):
    #Load keys
    try:
        keyfile = open('keys/kbpriv.pem')
    except:
        print "Please first run generatekeys.py"
        exit(0)
            
    keydata = keyfile.read()
    kbpriv = rsa.PrivateKey.load_pkcs1(keydata)
    print "Bill: Retrieved Bill's private key."
    
    with open('keys/kbpub.pem') as keyfile:
        keydata = keyfile.read()
    kbpub = rsa.PublicKey.load_pkcs1(keydata)
    print "Bill: Retrieved Bill's public key."
    
    with open('keys/kapub.pem') as keyfile:
        keydata = keyfile.read()
    kapub = rsa.PublicKey.load_pkcs1(keydata)
    print "Bill: Retrieved Ann's Public key."
    
    with open('keys/kcpriv.pem') as keyfile:
        keydata = keyfile.read()
    kcpriv = rsa.PrivateKey.load_pkcs1(keydata)
    print "Bill: Retrieved CA's Private key."
    
    print "Bill: Signing Kb+ with CA's Kc-..."
    signed_pub = rsa.sign(kbpub.save_pkcs1(), kcpriv, 'SHA-1')
    
    pack = []
    
    pack.append(kbpub.save_pkcs1())
    pack.append(signed_pub)
    
 
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind((host, port))
    s.listen(1)
    
    print "Bill: Server is running on port %d." % port
     
    while True:
        clientsock, clientaddr = s.accept()
        print "Bill: Received connection from ", clientsock.getpeername()
        
        #Sign Bill's key with Kc+...
        objList = pickle.dumps(pack)
        #send the public key over
        print "Bill: Sending signed key to Ann..."
        clientsock.send(objList)
        
        enchilada = ''
        while True:
            buf = clientsock.recv(1024)
            enchilada += buf
            if len(buf) < 1024:
                print "Bill: Received data from Ann... Unpacking now..."
                break
        #enchilada = ''
        try:
            enchilada = pickle.loads(enchilada)
        except EOFError:
            print "Bill: Something went awry with the pickling... Dropping connection. Try again."
            clientsock.close()
            continue
        
        print "Bill: Retrieving 3DES key, IV and other info..."
        cipher_bundle = pickle.loads(rsa.decrypt(enchilada[0], kbpriv))
        pw = cipher_bundle[0]
        mode = cipher_bundle[1]
        iv = cipher_bundle[2]
        pad = cipher_bundle[3]
        cipher = triple_des(pw, mode, iv, padmode=pad)
        print "Bill: Cipher created successfully!"
        print "Bill: Decrypting message payload with 3DES cipher..."
        msg_payload = pickle.loads(cipher.decrypt(enchilada[1], padmode=PAD_PKCS5))
        print "Bill: Decryption successful... Verifying hash..."
        try:
            rsa.verify(msg_payload[0], msg_payload[1], kapub)
            print "Bill: Success: Message successfully verified."
            msg = msg_payload[0]
            print "Bill: Ann says: " + msg
            clientsock.close()
        except:
            print "Bill: Error validating authenticity of message. Ann might be lying."
            clientsock.close()
            continue
    
    
def main(argv):
    
    host = ''
    port = 8080
    try:
        opts, args = getopt.getopt(argv,"i:p:",["host=", "port="])
    except getopt.GetoptError:
        print 'bob.py -i <host> -p <port>'
        start(host, port)
        sys.exit(1)
    for opt, arg in opts:
        if opt in ("-i", "--host"):
            host = arg
        elif opt in ("-p", "--port"):
            port = arg
    start(host, port)

if __name__ == '__main__':
    main(sys.argv[1:])    
    
    
    

    
