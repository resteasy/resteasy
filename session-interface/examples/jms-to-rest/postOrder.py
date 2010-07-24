import httplib, urlparse

conn = httplib.HTTPConnection("localhost:9095")
conn.request("HEAD", "/queues/jms.queue.orders")
res = conn.getresponse()
createLink = res.getheader("msg-create")
print createLink
conn.close()

createParsed = urlparse.urlparse(createLink)
conn = httplib.HTTPConnection(createParsed.netloc)
headers = {'Content-Type' : 'application/xml'}
xml = """<?xml version="1.0"?>
<order>
   <name>Bill</name>
   <amount>$199.99</amount>
   <item>iPhone4</item>
</order>"""
conn.request("POST", createParsed.path, xml, headers)
res = conn.getresponse()
print res.status, res.reason







