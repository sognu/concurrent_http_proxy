import pickle, string
import socket, random
import rsa, time
import sys, argparse, logging
from pyDes import *

  
def get_random():
    '''
    Generates an 8-byte sequence for the Initialization Vector for 3DES.
    Ascii letters and digits are valid.
    @return: 8-byte sequence of random ascii characters.
    '''
    size = 8 
    chars = string.ascii_letters + string.digits
    return ''.join(random.choice(chars) for x in range(size))
  
# Requests Kb+ from bob. The key is signed with Kc-. Use Kc+ to verify.
def get_kc_pub():
    try:
        keyfile = open('keys/kcpub.pem')
    except:
        print "Please first run generatekeys.py"
        exit(0)
    
    keydata = keyfile.read()
    return rsa.PublicKey.load_pkcs1(keydata)

def get_ka_priv():
    try:
        keyfile = open('keys/kapriv.pem')
    except:
        print "Please first run generatekeys.py"
        exit(0)
        
    keydata = keyfile.read()
    return rsa.PrivateKey.load_pkcs1(keydata)  

def start(mes, host, port, password):
    
    msg_payload = [] # Represents m + Ka-(H(m))
    enchilada = [] # Represents Ks(*) + Kb+(Ks)
    
    # Some variables...    
    iv = get_random()
    pad_mode = 2 # Used for pyDes.triple_des...
    block_mode = 'CBC'
    # Obtain the necessary certificates...
    logging.info("Ann: Retrieving Ka- from disk")
    kapriv = get_ka_priv()
    logging.info("Ann: Ka-:")
    
    logging.info("Ann: \nObtaining Kc+ from 'CA'")
    kcpub = get_kc_pub()
    logging.info("Ann: Kc+:")
    
    logging.info("Ann: Opening connection to Bill...") 
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((host, port))
    except:
        print "socket.error: Make sure Bill is running before Ann."
        exit(0)
        
    time.sleep(2)
    rcstring = ''
    while True:
        buf = s.recv(1024)
        rcstring += buf
        if len(buf) < 1024:
            break
    
    rcstring = pickle.loads(rcstring)

    logging.info("Ann: Received signed Kb+ from Bill... Now verifying authenticity...")
    # Decrypt kb+ using kc+
    try:
        rsa.verify(rcstring[0], rcstring[1], kcpub)
        logging.info("Ann: Successfully verified Kb+ authenticity.")
    except:
        logging.info("Ann: Error: Could not verify authenticity of Bill! He might be an impostor!")
        s.close()
        return None
    
    bobpub = rsa.PublicKey.load_pkcs1(rcstring[0])
    
    logging.info("Ann: Creating Message Payload (Message + Ka-(H(Message)))... ")
    # Hashed message, signed with Ann ka+.
    signed_mes = rsa.sign(mes, kapriv, 'SHA-1')
    logging.info("Ann: Message and Signed message:")
    msg_payload.append(mes)
    msg_payload.append(signed_mes)
    
    try:
        cipher = triple_des(password, block_mode, iv, None, padmode=pad_mode)
    except ValueError:
        logging.info("Ann: <password> must be 16 or 24 bits long. Assuming default...")
        password = b'passwordPASSWORD'
        cipher = triple_des(password, block_mode, iv, None, padmode=pad_mode)
    
    logging.info("Creating cipher bundle (password, block mode, IV, padding)...")
    cipher_bundle = [cipher.getKey(), cipher.getMode(), cipher.getIV(), cipher.getPadMode()]    
    # Kb+(Ks)
    logging.info("Ann: Encrypting Symmetric Key with Bill's public key...")
    bundledKey = rsa.encrypt(pickle.dumps(cipher_bundle), bobpub)
   
    # Ks(m + Ka(H(m))) 
    logging.info("Ann: Encrypting the message payload with the Symmetric Key...")
    secretBundle = cipher.encrypt(pickle.dumps(msg_payload), padmode=PAD_PKCS5)
    
    logging.info("Ann: Bundling the Symmetric key and encrypted message payload together, serializing and sending to Bill.")
    enchilada.append(bundledKey)
    enchilada.append(secretBundle)
    
    s.sendall(pickle.dumps(enchilada))
    logging.info("Ann: Successfully sent to Bill. Exiting...")
    s.close()
    return True

def main(argv):
    message = 'Goodbye from Ann!'
    password = 'passwordPASSWORD'
    log_level = logging.WARNING
       
    # Populate our options, -h/--help is already there for you.
    parser = argparse.ArgumentParser(description='Connects to Bill and sends a secure, encrypted message.')
    parser.add_argument("-i", "--info", help='Very decriptive output', action="store_true")
    parser.add_argument('-H', '--host', help='Provide an external hostname/IP. Defaults to localhost.', default='localhost')
    parser.add_argument('-p', '--port', help='Provide an external Port number. Defaults to 8080.', type=int, default=8080)
    parser.add_argument('-P', '--password', help='Provide a 16/24 bit 3DES password. ')
    parser.add_argument('-m', '--message', help='Message to send to Bill.')

    args = parser.parse_args()
    if args.info:
        log_level = logging.INFO
    if args.password:
        if len(args.password) is 16 or len(args.password) is 24:
            password = args.password
        else:
            logging.info("Invalid password length. Assuming default.")
    if args.message:
        message = args.message

    # Set up basic configuration, out to stderr with a reasonable default forma.
    logging.basicConfig(level=log_level)
    start(message, args.host, args.port, password)

if __name__ == '__main__':
    main(sys.argv[1:])
    
    
    
    
    
    
