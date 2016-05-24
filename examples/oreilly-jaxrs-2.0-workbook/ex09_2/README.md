HTTP Content Negotiation
=========================
This project is an example of using HTTP conneg to pick between XML and HTML and plain text using file name suffix
mappings provided by RESTEasy.

System Requirements:
-------------------------
- Maven 3.0.4 or higher

Building the project:
-------------------------
1. In root directory

mvn jetty:run

Use these links:

http://localhost:8080/services/customers/1
http://localhost:8080/services/customers/1.txt
http://localhost:8080/services/customers/1.html


This will build a WAR and run it with embedded Jetty
