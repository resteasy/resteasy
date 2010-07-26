import httplib, urlparse

conn = httplib.HTTPConnection("localhost:9095")
conn.request("HEAD", "/queues/jms.queue.orders")
res = conn.getresponse()
consumersLink = res.getheader("msg-pull-consumers")
consumersParsed = urlparse.urlparse(consumersLink)
conn = httplib.HTTPConnection(consumersParsed.netloc)
conn.request("POST", consumersParsed.path)
res = conn.getresponse()
consumeLink = res.getheader("msg-consume-next")
print consumeLink
conn.close()

headers = {"Accept-Wait" : "3", "Accept" : "application/xml"}

try:
    print "Waiting..."
    while True:
        createParsed = urlparse.urlparse(consumeLink)
        conn = httplib.HTTPConnection(createParsed.netloc)
        conn.request("POST", createParsed.path, None, headers)
        res = conn.getresponse()
        if res.status == 503:
            session = res.getheader("msg-session")
            consumeLink = res.getheader("msg-consume-next")
        elif res.status == 200:
            print "Success!"
            data = res.read()
            print data
            consumeLink = res.getheader("msg-consume-next")
            session = res.getheader("msg-session")
            print "Waiting"
        else:
            raise Exception('failed')
finally:
    if session != None:
        print "deleting hornetq session..."
        createParsed = urlparse.urlparse(session)
        conn = httplib.HTTPConnection(createParsed.netloc)
        conn.request("DELETE", createParsed.path)
        res = conn.getresponse()
        
        
    








