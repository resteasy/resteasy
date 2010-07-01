package org.jboss.resteasy.examples.oauth;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.util.HttpResponseCodes;

@Path("service")
public class MessagingService
{
   private String consumerId;
   private String consumerSecret;
   private volatile String callback;
      
   @Path("callbacks")
   @POST
   public Response registerCallbacks(@FormParam(OAuth.OAUTH_CONSUMER_KEY) String consumerId,
                                     @FormParam("xoauth_consumer_secret") String consumerSecret,
                                     @FormParam("callback") String callback) throws Exception {
       this.consumerId = consumerId;
       this.consumerSecret = consumerSecret;
       this.callback = callback;
       return Response.ok().build();
   }

   @Path("messages")
   @POST
   @Consumes("text/plain")
   public Response postMessages(String value) throws Exception {
       pushMessageToSubscriber(value);
       return Response.ok().build();
   }
   
   private void pushMessageToSubscriber(String message) throws Exception
   {
       HttpClient client = new HttpClient();
       PostMethod method = new PostMethod(getPushMessageURL());
       method.setRequestEntity(new StringRequestEntity(message, "text/plain", "UTF-8"));
       int status = client.executeMethod(method);
       if (HttpResponseCodes.SC_OK != status) {
          throw new RuntimeException("Message can not be delivered to subscribers");
       }
   }

   private String getPushMessageURL() 
      throws Exception {
      OAuthMessage message = new OAuthMessage("POST", callback, Collections.<Map.Entry>emptyList());
      OAuthConsumer consumer = new OAuthConsumer(null, consumerId, consumerSecret, null);
      OAuthAccessor accessor = new OAuthAccessor(consumer);
      message.addRequiredParameters(accessor);
      return OAuth.addParameters(message.URL, message.getParameters());
   }
}
