System Requirements:
You will need JDK 1.6 and Maven to run this example.  This example has been tested with Maven 2.2.1.  It may or may not work 
with earlier or later versions of Maven.


This is an example of mixing JMS producers and consumers with REST producers and consumers.  The REST clients have been
written in both Java using RESTEasy's client library and within the Python language.  You will need Python 2.6.1 or higher
to be able to run the Python clients.

To run the example you will need 5 shell-script windows (or you'll need to run 4 processes in background)

Step 1:
$ mvn jetty:run

This will bring up HornetQ and the HornetQ REST Interface.

Step 2:
$ mvn exec:java -Dexec.mainClass="RestReceive"

This will bring up a Java REST client that is continuously pulling the server through a consume-next (see doco for details).

Step 3:
$ mvn exec:java -Dexec.mainClass="JmsReceive"

This will bring up a Java JMS consumer that is using the MessageListener interface to consume messages.  It will
extract a Order instance from the JMS Message it receives.

Step 4:

$ python receiveOrder.py

This runs a very simple Python program to consume messages

Step 5:
Use one of these three commands to post messages to the system.  One of the receive clients will consume the message.

$ mvn exec:java -Dexec.mainClass="JmsSend"

A JMS client will create an Order object and send it to the queue.  You'll see one of the 4 clients receive the message.
Notice that the REST clients automatically cause the Order object to be transformed on the server and passed as XML
to the REST client.

$ mvn exec:java -Dexec.mainClass="RestSend"

THis is a REST client that uses the Acknowledgement protocol to receive a message from the queue.

$ python postOrder.py

This is a Python client that posts one message to the queue RESTfully (of course ;) )
