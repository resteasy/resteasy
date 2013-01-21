OAuth 1.0 2-leg flow
===================

Example demonstrating an OAuth 1.0 (http://tools.ietf.org/html/rfc5849)
like flow without end user UI being involved, similar to an OAuth 2-leg flow.
Note, this demo is a work in progress.
  

System Requirements:
-------------------------
- Maven 2.0.9 or higher

Building the project:
-------------------------

mvn clean install

or

mvn clean install -Dwebapp.port=8080

Default port is 9095.

Running the project and manually testing it:
-------------------------
- Run "mvn jetty:run" in one console. 
  Alternatively, copy target/example-oauth-push-messaging.war to a web/app container, example :
  "cp target/example-oauth-push-messaging.war $JBOSS_HOME/server/default/deploy" followed by
  "$JBOSS_HOME/bin/run.sh"   
- Execute the client :
  mvn exec:java -Dexec.mainClass=org.jboss.resteasy.examples.oauth.Subscriber
  
Demo Description
-------------------------

Typically, OAuth requires an end user to authorize a client before it can access the
resources it owns. Here is what RFC5849 says :

"OAuth provides a method for clients to access server resources on
 behalf of a resource owner (such as a different client or an end-
 user).  It also provides a process for end-users to authorize third-
 party access to their server resources without sharing their
 credentials (typically, a username and password pair), using user-
 agent redirections."

However, in a number of cases, having the end user to explicitly authorize the consumer
using some form of UI, repeatedly or even once, makes it difficult to apply OAuth to scenarious
where no human is expected to be involved.

OAuth 1.0 2-leg extension assumes that after a consumer has registered with the end server 
and the domain administrator has authorized the registered consumer to access the domain, no
subsequent interactions with the end user (resource owner) are required. 

This demo shows one approach toward enabling an OAuth 2-leg flow. It involves 3 parties : 
- Messaging Service (Pushes messages it receives to registered URIs, OAuth consumer/client)
- Messaging Service Subscriber (Registers callback URIs with the service, OAuth end user)
- Message Receiver (Callback URI points to it, OAuth server)
 
Note that the Subscriber knows how to log in into Messaging Service and Message Receiver
endpoints but Messaging Service does not know how to log in into the Message Receiver.  

All the URIs used in the demo are declared in resources/oauth.properties.
URIs

1. The subscriber starts with registering the Messaging Service on its behalf with the
   server :

POST http://localhost:9095/examples-oauth-push-messaging/oauth/registration
     ?oauth_consumer_key=http://www.messaging-service.com
and gets the consumer secret back.

2. The subscriber performs an administrative operation by allowing a registered "http://www.messaging-service.com"
consumer to access the URI pointing to a Message Receiver messages sink :

POST http://localhost:9095/examples-oauth-push-messaging/oauth/consumer-scopes
     ?oauth_consumer_key=http://www.messaging-service.com
      &xoauth_scope=http://localhost:9095/examples-oauth-push-messaging/rest/receiver/sink
      
This is a critical operation which can be performed by the subscriber agent without the human being involved.
The Message Receiver server needs to have its "/consumer-scopes" context protected only for the domain administrators
be able to authenticate.

3. The subscriber registers a callback URI with the Messaging Service, as well as the consumer id and secret
that the service will need to use when pushing the messages to a provided callback

POST https://localhost:9095/examples-oauth-push-messaging/rest/service/callbacks
     ?oauth_consumer_key=http://www.messaging-service.com
     &xoauth_consumer_secret=12345678 
     &callback=http://localhost:9095/examples-oauth-push-messaging/rest/receiver/sink
     
Note that a callback URI needs to start with a scope URI that the subscriber registered with the
Message Receiver at step 2.

4. Subscriber now acts as a message producer which pushes a message to the service :

POST http://localhost:9095/examples-oauth-push-messaging/rest/service/message
Content-Type : text/plain
Content-Length: 7

Hello !

4.1.1 The service chooses to push the messages to registered callbacks immediately so
it forwards the same message to a registered callback :

POST http://localhost:9095/examples-oauth-push-messaging/rest/receiver/sink
Authorization: OAuth
        oauth_consumer_key="http://www.third-party-service.com",
        oauth_token="",
        oauth_signature_method="HMAC-SHA1",
        oauth_timestamp="137131201",
        oauth_nonce="walatlh",
        oauth_signature="gKgrFCywp7rO0OXSjdot%2FIHF7IU%3D"
Content-Type : text/plain
Content-Length: 7

Hello !

Note that the service adds a signature using the consumer key and secret it was provided with
and also indicating that no access token is available. In this case the OAuthFilter will
ensure that the request URI (http://localhost:9095/examples-oauth-push-messaging/rest/receiver/sink)
matches against the scope registered at 2. If it does the it will also indirectly confirm
that the service has indeed been asked by the subscriber to push the messages given that
it was only a subscriber (the administrator) who could've registered this scope.

5. Subscriber requests a message it posted to the messaging service from the message receiver :

GET http://localhost:9095/examples-oauth-push-messaging/rest/receiver/message

and gets "Hello !"  