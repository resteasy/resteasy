
This project is an example of using HTTP conneg to pick between XML and HTML and plain text using file name suffix
mappings provided by RESTEasy.

System Requirements:
====================
- Maven 2.0.9 or higher

Building the project:
====================
1. In root directoy

mvn jetty:run

Use these links:

http://localhost:9095/customers/1
http://localhost:9095/customers/1.txt
http://localhost:9095/customers/1.html


This will build a WAR and run it with embedded Jetty
