System Requirements:
You will need JDK 1.6 and Maven to run this example.  This example has been tested with Maven 2.2.1.  It may or may not work 
with earlier or later versions of Maven.


This is an example of using duplicate detection for posted messages.  The first file to look at is:

src/main/resource/hornetq-rest.xml

You see that by default, all messages posted to msg-create URLs will follow the duplicate detection pattern talked
about in the documentation.

To run the example you will need 3 shell-script windows (or you'll need to run 2 processes in background)

Step 1:
$ mvn jetty:run

This will bring up HornetQ and the HornetQ REST Interface.

Step 2:
$ mvn exec:java -Dexec.mainClass="ReceiveOrder"

This will bring up a REST client that is continuously pulling the server through a consume-next (see doco for details).

Step 3:
$ mvn exec:java -Dexec.mainClass="PostOrder"

This class will post 3 orders.  The first order will cause the 307 redirection as stated in the docs.  A 2nd order
will be posted twice through the same consume-next URL.  You'll see from the ReceiveOrder process that only 2 messages
are actually processed through the queue (instead of the 3 posts that were done).

Step 4:

In Step 4, you will use the create-with-id URL published by the container.  To run the example, you must pass in
your own order id.  For example:

$ mvn exec:java -Dexec.mainClass="PostOrderWithId" -Dexec.args="001"

If you run this program with the same argument you'll see that only one of the messages passes through the queue
and is consumed by the ReceiveOrder process.  Pass a different string to -Dexec.args to post a new message that
isn't caught by the dup-detection facility.
