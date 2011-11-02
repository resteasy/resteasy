package org.jboss.resteasy.examples.oauth;

import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.HttpResponseCodes;


public class Subscriber
{
       
   private static final String ConsumerRegistrationURL;
   private static final String ConsumerScopesRegistrationURL;
   private static final String MessagingServiceCallbackRegistrationURL;
   private static final String MessagingServiceMessagesURL;
   private static final String MessageReceiverSinkURL;
   private static final String MessageReceiverGetURL;
   
   private static final String SubscriberOpenIdIdentifier;
   
   private static final String OpenIdTrustedRealmsURL;
   private static final String OpenIdTrustedRealm;
   
   private static final String MESSAGING_SERVICE_ID = "http://www.messaging-service.com";
   
   static {
       Properties props = new Properties();
       try {
           props.load(Subscriber.class.getResourceAsStream("/oauth.properties"));
       } catch (Exception ex) {
           throw new RuntimeException("oauth.properties resource is not available");
       }
       ConsumerRegistrationURL = props.getProperty("consumer.registration.url");
       ConsumerScopesRegistrationURL = props.getProperty("consumer.scopes.registration.url");
       MessagingServiceCallbackRegistrationURL = props.getProperty("messaging.service.callbacks.url");
       MessagingServiceMessagesURL = props.getProperty("messaging.service.messages.url");
       MessageReceiverSinkURL = props.getProperty("message.receiver.sink.url");
       MessageReceiverGetURL = props.getProperty("message.receiver.get.url");
       
       SubscriberOpenIdIdentifier = props.getProperty("subscriber.openid.identifier");
       OpenIdTrustedRealm = props.getProperty("subscriber.openid.trusted.realm");
       OpenIdTrustedRealmsURL = props.getProperty("openid.provider.realms.url");
       
   }
   

   
   public static void main(String [] args) throws Exception {
       Subscriber subscriber = new Subscriber();
       
       
       subscriber.registerTrustedOpenIdRealms();
       
       
       // 1.Register a messaging service (on its behalf) with our servers first
       String consumerSecret = subscriber.registerMessagingService(MESSAGING_SERVICE_ID);
       
       // 2.Register the scopes that the service will have a complete access to
       // Note 1 & 2 can be combined if needed
       subscriber.registerMessagingServiceScopes(MESSAGING_SERVICE_ID, MessageReceiverSinkURL);
       
       // 3. Now, register the consumer id, secret and the callback
       //    with the messaging service over HTTPS
       subscriber.registerMessagingServiceCallback(MESSAGING_SERVICE_ID, consumerSecret, MessageReceiverSinkURL);
       
       // 4. Act as as a producer and post few messages to the service
       subscriber.produceMessages();
       
       // 5. Finally, get this message from the message receiver server
       subscriber.getMessages();
       
       
   }
   
   public String registerMessagingService(String consumerKey) throws Exception
   {
      ClientRequest request = new ClientRequest(ConsumerRegistrationURL);
      request.header("Authorization", "OpenId " + SubscriberOpenIdIdentifier);
      request.formParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
      ClientResponse<String> response = request.post(String.class);
      if (HttpResponseCodes.SC_OK != response.getStatus()) {
         throw new RuntimeException("Registration failed");
      }
      // check that we got all tokens
      Map<String, String> tokens = OAuth.newMap(OAuth.decodeForm(response.getEntity()));
      String secret = tokens.get("xoauth_consumer_secret");
      if (secret == null) {
         throw new RuntimeException("No secret available");
      }
      return secret;
   }
   
   
   
   public void registerMessagingServiceScopes(String consumerKey, String scope) throws Exception
   {
      ClientRequest request = new ClientRequest(ConsumerScopesRegistrationURL);
      request.header("Authorization", "OpenId " + SubscriberOpenIdIdentifier);
      request.formParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
      request.formParameter("xoauth_scope", scope);
      request.formParameter("xoauth_permission", "sendMessages");
      ClientResponse<?> response = request.post();
      response.releaseConnection();
      if (HttpResponseCodes.SC_OK != response.getStatus()) {
         throw new RuntimeException("Scopes can not be registered");
      }
   }
   
   //TODO : the subscriber may need to provide some form of id known
   // to the message receiver so that the receiver can validate that it was indeed
   // the subscriber who asked the service to push the messages;
   // however, the consumerId creates by the subscriber can be enough;
   // Question : what about refresh tokens ?
   public void registerMessagingServiceCallback(String consumerKey, String consumerSecret, String callback) 
       throws Exception
   {
      ClientRequest request = new ClientRequest(MessagingServiceCallbackRegistrationURL);
      request.header("Authorization", "OpenId " + SubscriberOpenIdIdentifier);
      request.formParameter("consumer_id", consumerKey);
      request.formParameter("consumer_secret", consumerSecret);
      request.formParameter("callback_uri", callback);
      ClientResponse<?> response = request.post();
      response.releaseConnection();
      if (HttpResponseCodes.SC_OK != response.getStatus()) {
         throw new RuntimeException("Callback Registration failed");
      }
   }
   
   public void produceMessages() 
      throws Exception
   {
      ClientRequest request = new ClientRequest(MessagingServiceMessagesURL);
      request.header("Authorization", "OpenId " + SubscriberOpenIdIdentifier);
      request.body("text/plain", "Hello !");
      ClientResponse<?> response = request.post();
      response.releaseConnection();
      if (HttpResponseCodes.SC_OK != response.getStatus()) {
         throw new RuntimeException("Messages can not be sent");
      }
   }
   
   public void getMessages() 
       throws Exception
   {
      ClientRequest request = new ClientRequest(MessageReceiverGetURL);
      request.header("Authorization", "OpenId " + SubscriberOpenIdIdentifier);
      ClientResponse<String> response = request.post(String.class);
      response.releaseConnection();
      if (HttpResponseCodes.SC_OK != response.getStatus()) {
         throw new RuntimeException("Messages can not be received");
      }
      String message = response.getEntity();
      if (!"Hello !".equals(message))
      {
         throw new RuntimeException("Wrong Message");
      }
      System.out.println("Success : " + message);
   }
   
   public void registerTrustedOpenIdRealms() 
       throws Exception
    {
      ClientRequest request = new ClientRequest(OpenIdTrustedRealmsURL);
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      request.formParameter("xopenid.realm", OpenIdTrustedRealm);
      ClientResponse<?> response = request.post();
      response.releaseConnection();
      if (HttpResponseCodes.SC_OK != response.getStatus()) {
         throw new RuntimeException("OpenId realms can not be registered");
      }     
    }
}
