package org.jboss.resteasy.examples.oauth;

import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.util.HttpResponseCodes;


public class Subscriber
{
       
   private static final String ConsumerRegistrationURL;
   private static final String ConsumerScopesRegistrationURL;
   private static final String MessagingServiceCallbackRegistrationURL;
   private static final String MessagingServiceMessagesURL;
   private static final String MessageReceiverSinkURL;
   private static final String MessageReceiverGetURL;
   
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
       
       // 5. Finally, get this message from our server
       subscriber.getMessages();
   }
   
   public String registerMessagingService(String consumerKey) throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(ConsumerRegistrationURL);
      Base64 base64 = new Base64();
      String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
      method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
      method.addParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
      int status = client.executeMethod(method);
      if (HttpResponseCodes.SC_OK != status) {
          throw new RuntimeException("Registration failed");
      }
      // check that we got all tokens
      Map<String, String> response = OAuth.newMap(OAuth.decodeForm(method.getResponseBodyAsString()));
      String secret = response.get("xoauth_consumer_secret");
      if (secret == null) {
          throw new RuntimeException("No secret available");
      }
      return secret;
   }
   
   
   
   public void registerMessagingServiceScopes(String consumerKey, String scope) throws Exception
   {
       HttpClient client = new HttpClient();
       PostMethod method = new PostMethod(ConsumerScopesRegistrationURL);
       Base64 base64 = new Base64();
       String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
       method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
       method.addParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
       method.addParameter("xoauth_scope", scope);
       int status = client.executeMethod(method);
       if (HttpResponseCodes.SC_OK != status) {
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
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(MessagingServiceCallbackRegistrationURL);
      Base64 base64 = new Base64();
      String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
      method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
      method.addParameter("consumer_id", consumerKey);
      method.addParameter("consumer_secret", consumerSecret);
      method.addParameter("callback_uri", callback);
      int status = client.executeMethod(method);
      if (HttpResponseCodes.SC_OK != status) {
          throw new RuntimeException("Callback Registration failed");
      }
   }
   
   public void produceMessages() 
      throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(MessagingServiceMessagesURL);
      Base64 base64 = new Base64();
      String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
      method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
      method.setRequestEntity(new StringRequestEntity("Hello !", "text/plain", "UTF-8"));
      int status = client.executeMethod(method);
      if (HttpResponseCodes.SC_OK != status) {
          throw new RuntimeException("Messages can not be sent");
      }
   }
   
   public void getMessages() 
       throws Exception
   {
       HttpClient client = new HttpClient();
       GetMethod method = new GetMethod(MessageReceiverGetURL);
       Base64 base64 = new Base64();
       String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
       method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
       int status = client.executeMethod(method);
       if (HttpResponseCodes.SC_OK != status) {
           throw new RuntimeException("Messages can not be received");
       }
       String message = method.getResponseBodyAsString();
       if (!"Hello !".equals(message))
       {
           throw new RuntimeException("Wrong Message");
       }
       System.out.println("Success : " + message);
   }
}
