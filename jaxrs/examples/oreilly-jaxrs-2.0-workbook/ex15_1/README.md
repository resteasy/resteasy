@NameBinding and SecurityContext
========================
This project is a simple example that creates a custom one-time-password authentication protocol.  It uses ContainerRequestFilter and
ClientRequestFilter to implement this along with a @NameBinding to secure which parts you want to secure.  It also has
an authorization filter that allows you to specify how many times per day a user is allowed to invoke a method.

System Requirements:
-------------------------
- Maven 3.0.4 or higher

Building the project:
-------------------------
1. In root directory

mvn clean install

This will build a WAR and run it with embedded Jetty