Example demonstrating a complete OAuth 1.0 (http://tools.ietf.org/html/rfc5849)
protocol flow (3-leg OAuth). Note, this demo is a work in progress.
  

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
- Deploy the war 
 * Jetty
  Run "mvn jetty:run" in one console.
 * JBoss  
  update src/main/webapp/WEB-INF/web.xml so that OAuthBasicAuthentication filter is used
  instead of OAuthDBFilter (just update the comments in web.xml), rebuild and copy 
  target/example-oauth.war to JBoss :
  "cp target/example-oauth.war $JBOSS_HOME/server/default/deploy" followed by
  "$JBOSS_HOME/bin/run.sh"   
- Execute the client :
 * Jetty :
  mvn exec:java -Dexec.mainClass=org.jboss.resteasy.examples.oauth.EndUser -Djetty=true
 * JBoss :  
  mvn exec:java -Dexec.mainClass=org.jboss.resteasy.examples.oauth.EndUser
  
Note that when JBoss is used, a JBoss Security based filter (OAuthBasicAuthentication) 
is used to perform both OAuth (by delegating to its superclass) and Basic Authentication.   
  
Demo Description
================

From http://tools.ietf.org/html/rfc5849 :

"OAuth provides a method for clients to access server resources on
 behalf of a resource owner (such as a different client or an end-
 user).  It also provides a process for end-users to authorize third-
 party access to their server resources without sharing their
 credentials (typically, a username and password pair), using user-
 agent redirections."
 
This demo shows the interaction between the end user (org.jboss.resteasy.examples.oauth.EndUser),
the 3rd party consumer (org.jboss.resteasy.examples.oauth.ConsumerResource) and 
and the server (org.jboss.resteasy.examples.oauth.ServiceProviderResource) which the consumer
is accessing on behalf of the end user.

Note that the end user is a Java client and it handles the redirection by checking
the HTTP response status codes. Also it invokes a simple UI panel in order to handle
the token authorization requests issued by the server.

All the URIs used in the demo are declared in resources/oauth.properties.
URIs

1. The end user starts with issuing a request to the 3rd party consumer web application :

- POST http://localhost:9095/examples-oauth/rest/consumer/end-user-service
       ?xoauth_scope=http://localhost:9095/examples-oauth/rest/server

The end user asks the 3rd party consumer to access the resource at the xoauth_scope URI
on its behalf and do something useful with it that this consumer can do. Note that parameters
such as "xoauth_scope" are prepended with 'x' (extensions).

1.1. The consumer registers itself with the server - note, in the reality the consumer
will do it well in advance :

POST http://localhost:9095/examples-oauth/oauth/registration
     ?oauth_consumer_key=http://www.third-party-service.com 
  
and gets the shared secret back.

2. Consumer asks the server to allocate a temporarily request token and uses its shared secret
to sign:

POST http://localhost:9095/examples-oauth/oauth/requestToken
Authorization: OAuth 
        oauth_consumer_key="http://www.third-party-service.com",
        oauth_signature_method="HMAC-SHA1",
        oauth_timestamp="137131200",
        oauth_nonce="wIjqoS",
        oauth_callback="http://localhost:9095/examples-oauth/rest/consumer/token-authorization",
        oauth_signature="74KNZJeDHnMBp0EMJ9ZHt%2FXKycU%3D",
        xoauth_scope=http://localhost:9095/examples-oauth/rest/server
        
All the values are shown without them being encoded for the simplicity.
 
Note that oauth_callback points to the address where the end user will eventually post
the verifier authorizing the request token.

3. The server allocates a request token (example, 123456789) and returns it to the consumer, alongside with the
request token secret.

4. Consumer replies to the end user's request (at step 1) by redirecting it to a token authorization
URI where the end user should authorize the temporarily request token:

HTTP 1.1 303
Location : http://localhost:9095/examples-oauth/oauth/authorization?oauth_token=123456789 

5. The end user follows the redirection URI but also indicates to the server that it
expects an XML-formatted token authorization request :

GET http://localhost:9095/examples-oauth/oauth/authorization?oauth_token=123456789
Accept: application/xml
Authorization: Basic ...

6. The server replies with :

<tokenAuthorizationRequest xmlns="http://org.jboss.com/resteasy/oauth"
 replyTo="http://localhost:9095/examples-oauth/oauth/authorization-confirm?oauth_token=123456789">
 <consumerId>http://www.third-party-service.com</consumerId>
 <requestScope>http://localhost:9095/examples-oauth/rest/server</requestScope>
</tokenAuthorizationRequest>  

7. The end user agent (Java client) pops up a UI panel asking the user if "http://www.third-party-service.com"
is allowed to access "http://localhost:9095/examples-oauth/rest/server"

8. After the end user says 'yes' the Java agent replies to the server :

POST http://localhost:9095/examples-oauth/oauth/authorization-confirm
      ?oauth_token=123456789&xoauth_end_user_decision=yes
Authorization: Basic ...

Note that at steps 5 and 8 the end user authenticates itself to the server so that
it can indeed authorize the consumer's tokens
        
9. The server acknowledges the end user's decision and redirects the end user
back to the consumer, using the callback URI the consumer provided earlier on :

HTTP 1.1 303
Location : http://localhost:9095/examples-oauth/rest/consumer/token-authorization
           ?oauth_token=123456789&oauth_verifier=98765431 
    
Note that the server appends the token which has been authorized and the verifier.
The consumer will use this verifier when exchanging the authorized request token
for the access token.
Also, if the consumer did not provide the callback URI at the earlier step then the server
would just return the verifier to the end user and the end user will post the verifier to the
consumer using out of band approach.

10. End user checks the status and follows the redirection URI.

11. The consumer exchanges the authorized request token for the access token :

POST http://localhost:9095/examples-oauth/oauth/accessToken
Authorization: OAuth
        oauth_consumer_key="http://www.third-party-service.com",
        oauth_token="123456789",
        oauth_signature_method="HMAC-SHA1",
        oauth_timestamp="137131201",
        oauth_nonce="walatlh",
        oauth_verifier="987654321",
        oauth_signature="gKgrFCywp7rO0OXSjdot%2FIHF7IU%3D"
    
12. The server sends back the access token (example, "ABCDE") and the secret

13. Consumer accesses the resources at http://localhost:9095/examples-oauth/rest/server
but using the access token this time to create an OAuth signature

GET http://localhost:9095/examples-oauth/rest/server

and finally replies to the end user at step 10

14. End users prints :  "Success : Resource1" where "Resource1" is the representation of the resource
which the end user owns but accessed by the consumer on its behalf.

