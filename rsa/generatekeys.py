import rsa
import os

#Generates pub/priv keypair of n bytes
def generate_keypair(n):
    (publickey, privatekey) = rsa.newkeys(n)
    return {"pub": publickey, "priv": privatekey}
    
# Write the key pair to disk
def write_keys(keys, name):
    
    if not os.path.exists('./keys'):
        os.makedirs('./keys')
    try:
        fi = open('keys/'+name+'pub.pem', 'w')
        fi.write(keys['pub'].save_pkcs1('PEM'))
        fi.close()
    except:
        print "false 1"
        return False
    try:
        f = open('keys/'+name+'priv.pem', 'w')
        f.write(keys['priv'].save_pkcs1('PEM'))
        f.close()
    except:
        print "false 2"
        return False
    return True

write_keys(generate_keypair(1024), 'ka')
write_keys(generate_keypair(1024), 'kb')
write_keys(generate_keypair(2048), 'kc')    
