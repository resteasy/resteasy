package org.jboss.resteasy.examples.oauth;

import net.oauth.OAuth;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.HttpResponseCodes;

import java.util.Map;
import java.util.Properties;


public class Subscriber
{
       
   private static final String ConsumerRegistrationURL;
   private static final String ConsumerScopesRegistrationURL;
   private static final String MessagingServiceCallbackRegistrationURL;
   private static final String MessagingServiceMessagesURL;
   private static final String MessageReceiverSinkURL;
   private static final String MessageReceiverGetURL;
   private static final String MessageReceiverSubscriberGetURL;
   
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
       MessageReceiverSubscriberGetURL = props.getProperty("message.receiver.subscriber.get.url");
   }
   

   
   public static void main(String [] args) throws Exception {
       Subscriber subscriber = new Subscriber();
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
       
       // Now, ask the subscriber capable of acting as a receiver to repeat the same sequence
       // and get the message it receives
       subscriber.getMessagesFromSubscriberReceiver();
   }
   
   public String registerMessagingService(String consumerKey) throws Exception
   {
      ClientRequest request = new ClientRequest(ConsumerRegistrationURL);
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      request.formParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
      ClientResponse<String> response = null;
      try {
         response = request.post(String.class);
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
      } finally {
         response.releaseConnection();
      }
   }
   
   
   
   public void registerMessagingServiceScopes(String consumerKey, String scope) throws Exception
   {
      ClientRequest request = new ClientRequest(ConsumerScopesRegistrationURL);
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      request.formParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
      request.formParameter("xoauth_scope", scope);
      request.formParameter("xoauth_permission", "sendMessages");
      ClientResponse<?> response = null;
      try {
         response = request.post();
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Scopes can not be registered");
         }
      } finally {
         response.releaseConnection();
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
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      request.formParameter("consumer_id", consumerKey);
      request.formParameter("consumer_secret", consumerSecret);
      request.formParameter("callback_uri", callback);
      ClientResponse<?> response = null;
      try {
         response = request.post();
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Callback Registration failed");
        }
      } finally {
         response.releaseConnection();
      }
   }
   
   public void produceMessages() 
      throws Exception
   {
      ClientRequest request = new ClientRequest(MessagingServiceMessagesURL);
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      request.body("text/plain", "Hello !");
      ClientResponse<?> response = null;
      try {
         response = request.post();
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Messages can not be sent");
         }
      } finally {
         response.releaseConnection();
      }
   }
   
   public void getMessages() 
       throws Exception
   {
      ClientRequest request = new ClientRequest(MessageReceiverGetURL);
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      ClientResponse<String> response = null;
      try {
         response = request.get(String.class);
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Messages can not be received");
         }
         String message = response.getEntity();
         if (!"Hello !".equals(message))
         {
            throw new RuntimeException("Wrong Message");
         }
         System.out.println("Success : " + message);
      } finally {
         response.releaseConnection();
      }
   }
   
   public void getMessagesFromSubscriberReceiver() throws Exception
   {
      ClientRequest request = new ClientRequest(MessageReceiverSubscriberGetURL);
      String base64Credentials = new String(Base64.encodeBytes("admin:admin".getBytes()));
      request.header("Authorization", "Basic " + base64Credentials);
      ClientResponse<String> response = null;
      try {
         response = request.get(String.class);
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Messages can not be received");
         }
         String message = response.getEntity();
         if (!"Hello2 !".equals(message))
         {
            throw new RuntimeException("Wrong Message");
         }
         System.out.println("Message from the subscriber-receiver : " + message);
      } finally {
         response.releaseConnection();
      }
    }
}
