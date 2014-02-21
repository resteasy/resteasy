Test automatic adding of resteasy-spring dependency as resource root by server (EAP 6.3 / Wildfly)

To run test from current module directory
	
	mvn clean verify -Djboss.home=JBOSS_HOME
	#eg. mvn clean verify -Djboss.home=/home/development/jbossqe/JBEAP-6.3.0.DR0/build/jboss-eap-6.3
	
Notes:
To install run arquillian-deployment-scenario-provider dependency into local maven repository
	mvn install -pl :arquillian-deployment-scenario-provider from top level directory

To run test from top level directory
	mvn clean verify -pl :resteasy-spring-jboss -Dresteasy-spring-jboss -Djboss.home=JBOSS_HOME
	#eg. mvn clean verify -pl :resteasy-spring-jboss -Dresteasy-spring-jboss -Djboss.home=/home/development/jbossqe/JBEAP-6.3.0.DR0/build/jboss-eap-6.3
