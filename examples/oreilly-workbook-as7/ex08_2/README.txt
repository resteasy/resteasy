
This project is an example of using HTTP conneg to pick between XML and HTML and plain text using file name suffix
mappings provided by RESTEasy.

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

Use these links:

http://localhost:8080/ex08_2/services/customers/1
http://localhost:8080/ex08_2/services/customers/1.txt
http://localhost:8080/ex08_2/services/customers/1.html

4. mvn jboss-as:undeploy

Undeploys the WAR

