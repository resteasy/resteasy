package org.jboss.resteasy.examples.oauth;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import net.oauth.OAuth;

@Path("service")
public class MessagingService
{
   private static final String DEFAULT_SENDER_ID = "http://www.messaging-service.com"; 
   private MessageSender sender; 
    
   private volatile String messageSenderId;
   private volatile String callbackURI;
      
   public MessagingService() {
       // will be injected/configured
       sender = new OAuthMessageSender();
   }
   
   
   @Path("callbacks")
   @POST
   public Response registerCallbackURI(@FormParam(OAuth.OAUTH_CONSUMER_KEY) String consumerId,
                                       @FormParam("callback") String callback) throws Exception {
       this.messageSenderId = consumerId == null ? consumerId : DEFAULT_SENDER_ID;
       this.callbackURI = callback;
       return Response.ok().build();
   }

   @Path("messages")
   @POST
   @Consumes("text/plain")
   public Response receiveMessages(String message) throws Exception {
       // handle them as needed and
       // forward them to subscribers
       sender.sendMessage(callbackURI, messageSenderId, message);
       return Response.ok().build();
   }
   
   
}
