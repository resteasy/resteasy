Test automatic adding of resteasy-spring dependency as resource root by server (EAP 6.3 / Wildfly)

1. Deployment contains spring dependencies

a) to run test from current module directory (server already installed)
	
	mvn clean verify -Djboss.home=JBOSS_HOME
	#eg. mvn clean verify -Djboss.home=/home/development/jbossqe/JBEAP-6.3.0.DR1/build/jboss-eap-6.3
	
	#run with different version of resteasy-spring and spring
	mvn clean verify -Djboss.home=JBOSS_HOME -Dversion.org.jboss.resteasy=2.3.8.Final-redhat-1 -Dversion.org.springframework=4.0.0.RELEASE

2. Spring is installed as server module, deployment does not contain spring dependencies

a) To run test from current module directory when server with spring module is already installed
	
	mvn clean verify -Djboss.home=JBOSS_HOME -Dspring-in-module
	#eg. mvn clean verify -Dspring-in-module -Djboss.home=/home/development/jbossqe/JBEAP-6.3.0.DR0/build/jboss-eap-6.3
	
b) To install spring module and run test from current module directory when server is already installed
	
	mvn verify -Dinstall-as-module-spring-3.2.x -Dspring-in-module -Djboss.home=JBOSS_HOME

	Note: this will change server installation, it will install spring module

c) To download server, unpack, install spring module and run test from current module directory 
	mvn clean verify -Dinstall-as-module-spring-3.2.x -Dspring-in-module -Dinstall-jboss
	

Notes:

alternative to specifying dependency on spring module in MANIFEST.MF

	definition of property -Duse-jboss-deployment-structure causes to add to deployment as WEB-INF resource file jboss-deployment-structure

Example of VM arguments for debugging in Eclipse
	
	-DallowConnectingToRunningServer=true -Djboss.home=/home/development/jbossqe/JBEAP-6.3.0.DR2/build/jboss-eap-6.3 -Dspring-in-module -Djboss.options="-Xmx512m -XX:MaxPermSize=128m -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y" -Duse-jboss-deployment-structure

When maven fails because of missing dependency arquillian-deployment-scenario-provider, install it into local maven repository by running following command in top level directory
	
	mvn install -pl :arquillian-deployment-scenario-provider

To run test from top level directory append -pl :resteasy-spring-jboss

	mvn clean verify -pl :resteasy-spring-jboss -Dresteasy-spring-jboss -Djboss.home=JBOSS_HOME
	#eg. mvn clean verify -pl :resteasy-spring-jboss -Dresteasy-spring-jboss -Djboss.home=/home/development/jbossqe/JBEAP-6.3.0.DR0/build/jboss-eap-6.3

Downloaded server is unpacked into target folder
