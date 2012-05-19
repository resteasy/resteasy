
This project is a simple example showing usage of JAX-RS injection annotations.

System Requirements:
====================
- Maven 2.0.9 or higher
- JBoss AS 7.1.1 or higher

Building the project:
====================
1. Start JBoss AS 7 manually
2. In root directory of project

mvn clean install

This will build a WAR.

2. mvn jboss-as:deploy

This will manually deploy the WAR to jboss

3. Then open browser and go to:

http://localhost:8080/ex05_2

Submit form and follow links.

4. mvn jboss-as:undeploy

Undeploys the WAR
