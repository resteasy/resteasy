import httplib, urlparse
from M2Crypto import BIO, SMIME, X509

conn = httplib.HTTPConnection("localhost:9095")
conn.request("GET", "/smime/signed")
res = conn.getresponse()
if res.status != 200:
   print res.status
   raise Exception("Failed to connect")

contentType = res.getheader("content-type")
data = res.read()

# Need to reconstruct a Mail message with content type
# as SMIME wants it in that format
bio = BIO.MemoryBuffer("Content-Type: ")
bio.write(contentType)
bio.write("\r\n\r\n")
bio.write(data)

s = SMIME.SMIME()

# Load the signer's cert.
x509 = X509.load_cert('src/main/resources/cert.pem')
sk = X509.X509_Stack()
sk.push(x509)
s.set_x509_stack(sk)

# Load the signer's CA cert. In this case, because the signer's
# cert is self-signed, it is the signer's cert itself.
st = X509.X509_Store()
st.load_info('src/main/resources/cert.pem')
s.set_x509_store(st)

# Load the data, verify it.
p7, data = SMIME.smime_load_pkcs7_bio(bio)
v = s.verify(p7, data)
print v
