Example demonstrating an OAuth 1.0 (http://tools.ietf.org/html/rfc5849)
like flow without end user UI being involved, similar to an OAuth 2-leg flow.

This demo is practically identical to examples/oauth-push-messaging demo with the main difference being that
OpenId identifiers are used by Subscriber to authenticate itself with MessagingService, MessageReceiver and OAuth servers.

Note, this demo is a work in progress.
 

System Requirements:
====================
- Maven 2.0.9 or higher

Building the project:
====================

mvn clean install

or

mvn clean install -Dwebapp.port=8080

Default port is 9095.

Running the project and manually testing it:
===========================================
- Run "mvn jetty:run" in one console. 
  Alternatively, copy target/example-oauth-push-messaging-sso.war to a web/app container, example :
  "cp target/example-oauth-push-messaging-sso.war $JBOSS_HOME/server/default/deploy" followed by
  "$JBOSS_HOME/bin/run.sh"   
- Execute the client :
  mvn exec:java -Dexec.mainClass=org.jboss.resteasy.examples.oauth.Subscriber
  
Demo Description
================

Please see examples/oauth-push-messaging/README.txt for the description of how Subcriber interacts 
with the MessagingService, MessageReceiver and OAuth servers.

The only difference here is that instead of using a name/password pair to authenticate itself with all the servers, 
Subscriber uses its OpenId identifier in the form of "http://localhost:{webapp.port}/provider-servlet/{name}", example,
http://localhost:9096/provider-servlet/admin.

MessagingService, MessageReceiver and OAuth servers are all additionally protected by OpenId filters which authenticate
Subscriber.

Thus Subscriber only needs to know about its OpenId identifier.

Please see examples/oauth-push-messaging-sso/openid demo module where a sample OpenId provider is shipped. 
This provider is assuming that only a checkid_immediate OpenId mode is supported without requiring a user to do additional logins.
 
Subscriber has to login at least once and provide the OpenId server with the list of relying parties it trusts.
This lets the OpenId server to avoid requiring a user to do another login when dealing with checkid_immediate requests.
