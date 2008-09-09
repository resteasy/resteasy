Example of using RestEasy with:
- JMS
- Sub Resource Locators
- custom assembly of root resources

This project is a facade over JMS.  See docs.html for more information on the project

System Requirements:
====================
- Maven 2.0.9 or higher
- Requires JBoss 4.2.x or higher (you'll have to modify the POMs to get it to work with another EE 5 compatible application server)

Building the project:
====================
1. Edit pom.xml in the root directory.  Change the <jbossHome> property to the location of your JBoss installation
2. In root directoy

mvn clean install

This will build a WAR (for resteasy) and run a bunch of unit tests.

