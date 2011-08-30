import httplib, urlparse
import re
from M2Crypto import BIO, Rand, SMIME, X509


data = """Content-Type: application/xml

<customer name="bill"/>
"""
# Make a MemoryBuffer of the message.


buf = BIO.MemoryBuffer(data)

# Seed the PRNG.
Rand.load_file('randpool.dat', -1)

# Instantiate an SMIME object.
s = SMIME.SMIME()
s.load_key('src/main/resources/private.pem', 'src/main/resources/cert.pem')
p7 = s.sign(buf, SMIME.PKCS7_DETACHED)

out = BIO.MemoryBuffer()
buf = BIO.MemoryBuffer(data)
s.write(out, p7, buf)

# Extract the content-type and multipart message. I can't figure out a better way to do this
# This is kind of a hack, but I couldn't figure out how to just
# get the body and headers separately or just send the BIO directly thru the HTTP connection
l = out.readline()
l = out.readline()

result = re.match('Content-Type: (.*)', l)
contentType = result.group(1)

l = out.readline()
l = out.readline()

o = out.read()

# Finally send the message
conn = httplib.HTTPConnection("localhost:9095")
headers = {"Content-Type" : contentType}


conn.request("POST", "/smime/signed", o, headers)
res = conn.getresponse()
print res.status, res.reason

