JAX-RS and EJB
================
This project is an example of using JAX-RS, EJB, and JPA all together in one application through a JAX-RS
portable way.

System Requirements:
-------------------------
- Maven 3.0.4 or higher

Building the project:
-------------------------
1. Start a JBoss 7.1.1 instance in the background
2. In root directory

mvn clean install

This will build a WAR, deploy it on JBoss, and then run the testsuite
