Example of using RestEasy with:
- JMS
- Sub Resource Locators
- custom assembly of root resources

This project is a facade over JMS.  See docs.html for more information on the project

System Requirements:
====================
- Maven 2.0.9 or higher
- Requires JBoss 4.2.x or higher (you'll have to modify the POMs to get it to work with another EE 5 compatible application server)
- You'll also have to turn off JMS security.  In the JBoss deploy directory edit jms/jbossmq-service.xml.  Comment out the NextInterceptor attribute and replace it with DestinationManager.  See below:

  <mbean code="org.jboss.mq.server.jmx.InterceptorLoader" name="jboss.mq:service=TracingInterceptor">
    <attribute name="InterceptorClass">org.jboss.mq.server.TracingInterceptor</attribute>
      <depends optional-attribute-name="NextInterceptor">jboss.mq:service=DestinationManager</depends>
<!--    <depends optional-attribute-name="NextInterceptor">jboss.mq:service=SecurityManager</depends>  -->
  </mbean>

Building the project:
====================
1. Edit pom.xml in the root directory.  Change the <jbossHome> property to the location of your JBoss installation
2. In root directoy

mvn clean install

This will build a WAR (for resteasy) and run a bunch of unit tests.

