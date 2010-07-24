import httplib, urlparse

conn = httplib.HTTPConnection("localhost:9095")
conn.request("HEAD", "/queues/jms.queue.orders")
res = conn.getresponse()
consumeLink = res.getheader("msg-consume-next")
print consumeLink
conn.close()

headers = {"Accept-Wait" : "3"}

try:
    while True:
        createParsed = urlparse.urlparse(consumeLink)
        conn = httplib.HTTPConnection(createParsed.netloc)
        print "Waiting..."
        conn.request("POST", createParsed.path, None, headers)
        res = conn.getresponse()
        if res.status == 503:
            print "Timeout on receive, retrying."
            session = res.getheader("msg-session")
            consumeLink = res.getheader("msg-consume-next")
        elif res.status == 200:
            data = res.read()
            print data
            consumeLink = res.getheader("msg-consume-next")
            session = res.getheader("msg-session")
        else:
            raise Exception('failed')
finally:
    if session != None:
        print "deleting hornetq session..."
        createParsed = urlparse.urlparse(session)
        conn = httplib.HTTPConnection(createParsed.netloc)
        conn.request("DELETE", createParsed.path)
        res = conn.getresponse()
        
        
    








