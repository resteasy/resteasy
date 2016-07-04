DKIM/Doseta Message signing
=================

Two example projects that will add a DKIM-Signature header to sign message bodies transferred from client and server
One project uses DNS to obtain the public key to verify signatures, one doesn't.

System Requirements:
--------------------
* Maven 2.0.9 or higher

Building the project:
------------------------
1. In root directoy

mvn clean install

This will build a WAR and run it with embedded Jetty