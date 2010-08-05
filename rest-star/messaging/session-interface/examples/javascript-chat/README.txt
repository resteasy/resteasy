System Requirements:
You will need JDK 1.6, Maven, and a browser to run this example.  This example has been tested with Maven 2.2.1.  It may or may not work
with earlier or later versions of Maven.


This is an example of producing and consuming messages through a topic.  The client is Javascript code within your browser.
The example is a very simple chat application between two browser windows.

Step 1:
$ mvn jetty:run

This will bring up HornetQ and the HornetQ REST Interface.

Step 2:
Bring up two browsers and point them to http://localhost:9095.  In the textbox type a message you want to send.  Click
the "Click to send message" button and you'll see the message show up in both browser windows.