import httplib, urlparse
from M2Crypto import BIO, SMIME, X509

conn = httplib.HTTPConnection("localhost:9095")
conn.request("GET", "/smime/encrypted")
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
s.load_key('src/main/resources/private.pem', 'src/main/resources/cert.pem')

p7, d = SMIME.smime_load_pkcs7_bio(bio)
out = s.decrypt(p7)

print "--- Received Data ---"
# It may contain headers like Content-Type, so you'll have to parse it
print out