System Requirements:
You will need JDK 1.6 and Maven to run this example.  This example has been tested with Maven 2.2.1.  It may or may not work 
with earlier or later versions of Maven.


This is an example of having the HornetQ REST interface forward a posted message to a registered URL.

To run the example you will need 3 shell-script windows (or you'll need to run 2 processes in background)

Step 1:
$ mvn jetty:run

This will bring up HornetQ and the HornetQ REST Interface.  Two queues will be created.  An "order" queue and a "shipping"
queue.  The server will forward posted messages to the "shipping" queue through a registered push subscription.

Step 2:
$ mvn exec:java -Dexec.mainClass="ReceiveShipping"

This will bring up a JMS client registers a MessageListener consumer to receive Order objects.  It will automatically
convert a posted HTTP message into an Order object using JAX-RS content handlers.

Step 3:
$ mvn exec:java -Dexec.mainClass="PushReg"

This creates a push registration that listens on the "order" queue and forwards messages posted to it to a URL.  This
URL is the REST resource of the "shipping" queue.

Step 4:

$ mvn exec:java -Dexec.mainClass="PostOrder"

This posts an order to the "order" queue.  You'll see it eventually consumed by the ReceiveShipping process.
