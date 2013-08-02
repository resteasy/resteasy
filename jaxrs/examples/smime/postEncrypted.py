import httplib, urlparse
from M2Crypto import BIO, Rand, SMIME, X509

# Make a MemoryBuffer of the message.
buf = BIO.MemoryBuffer(
"""Content-Type: application/xml

<customer name="bill"/>
""")

# Seed the PRNG.
Rand.load_file('randpool.dat', -1)

# Instantiate an SMIME object.
s = SMIME.SMIME()

# Load target cert to encrypt to.
x509 = X509.load_cert('src/main/resources/cert.pem')
sk = X509.X509_Stack()
sk.push(x509)
s.set_x509_stack(sk)

# Set cipher: 3-key triple-DES in CBC mode.
s.set_cipher(SMIME.Cipher('des_ede3_cbc'))

# Encrypt the buffer.
p7 = s.encrypt(buf)

out = BIO.MemoryBuffer()
s.write(out, p7)

# Strip out junk, I can't figure out a better way to do this
# This is kind of a hack, but I couldn't figure out how to just
# get the body without the headers, or just send the BIO directly thru the HTTP connection
# s.write(out, py) adds a bunch of headers and doesn't just output the body
# so, I read 5 lines then use the rest of the buffer to initalize the string

out.readline()
out.readline()
out.readline()
out.readline()
out.readline()
o = out.read()

# This is an alternative way to extract the body
#p7.write(out)
#o = out.read()
#o = o.replace('-----BEGIN PKCS7-----', '')
#o = o.replace('-----END PKCS7-----', '')
#o = o.strip()

# Finally send the message
conn = httplib.HTTPConnection("localhost:9095")
headers = {"Content-Disposition" : "attachment; filename=\"smime.p7m\"",
           "Content-Type" : "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"",
           "Content-Transfer-Encoding" :"base64"}


conn.request("POST", "/smime/encrypted", o, headers)
res = conn.getresponse()
print res.status, res.reason

