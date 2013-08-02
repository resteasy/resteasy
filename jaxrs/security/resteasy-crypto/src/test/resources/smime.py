from M2Crypto import BIO, Rand, SMIME, X509
    
def makebuf(text):
   return BIO.MemoryBuffer(text)
    
# Make a MemoryBuffer of the message.
#buf = makebuf('a sign of our times')
buf = makebuf(
"""Content-Type: application/xml

<customer name="bill"/>
""")

# Seed the PRNG.
Rand.load_file('randpool.dat', -1)

# Instantiate an SMIME object.
s = SMIME.SMIME()
    
# Load target cert to encrypt to.
x509 = X509.load_cert('mycert.pem')
sk = X509.X509_Stack()
sk.push(x509)
s.set_x509_stack(sk)
   
# Set cipher: 3-key triple-DES in CBC mode.
s.set_cipher(SMIME.Cipher('des_ede3_cbc'))
    
# Encrypt the buffer.
#p7 = s.encrypt(buf, SMIME.PKCS7_TEXT)
p7 = s.encrypt(buf)

# Output p7 in mail-friendly format.
out = BIO.MemoryBuffer()
out.write('From: sender@example.dom\n')
out.write('To: recipient@example.dom\n')
out.write('Subject: M2Crypto S/MIME testing\n')
s.write(out, p7)
    
print out.read()

#out = BIO.MemoryBuffer()
#p7.write(out)
#print out.read()
#print '------------------\n'


#out = BIO.MemoryBuffer()
#s.write(out, p7)

# Instantiate an SMIME object.
#s = SMIME.SMIME()

# Load private key and cert.
#s.load_key('mycert-private.pem', 'mycert.pem')

# Load the encrypted data.
#p7, data = SMIME.smime_load_pkcs7_bio(out)

# Decrypt p7.
#out = s.decrypt(p7)

#print out

