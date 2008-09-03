Example of using RestEasy with:
- EJBs
- EARs

This project is a simple example of 2 EJBs that expose 2 RESTFul web services by using JAX-RS and the RESTEasy EJB
integration.  EJBs are used as both as root resources, and sub-resources.

System Requirements:
====================
- Maven 2.0.9 or higher
- Requires JBoss 4.2.x or higher (you'll have to modify the POMs to get it to work with another EE 5 compatible application server)

Building the project:
====================
1. Edit pom.xml in the root directory.  Change the <jbossHome> property to the location of your JBoss installation
2. In root directoy

mvn clean install

This will build an EJB, a WAR (for resteasy), and an EAR.  The ear module will deploy the ear file into JBoss
and run the testsuite provided in the ear module.

