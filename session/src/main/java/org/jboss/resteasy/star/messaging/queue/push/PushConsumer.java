package org.jboss.resteasy.star.messaging.queue.push;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.HttpMessage;
import org.jboss.resteasy.star.messaging.queue.push.xml.BasicAuth;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;
import org.jboss.resteasy.star.messaging.queue.push.xml.XmlHttpHeader;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushConsumer implements MessageHandler
{
   private PushRegistration registration;
   private boolean authenticated;
   private HttpClient client = new HttpClient();
   private ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor(client);
   private Link nextPost;
   private String method;
   private String contentType;
   protected ClientSessionFactory factory;
   protected ClientSession session;
   protected ClientConsumer consumer;
   protected String destination;
   protected String id;

   public PushConsumer(ClientSessionFactory factory, String destination, String id, PushRegistration registration)
   {
      this.factory = factory;
      this.destination = destination;
      this.id = id;
      this.registration = registration;
   }

   public PushRegistration getRegistration()
   {
      return registration;
   }

   public void start() throws Exception
   {
      if (registration.getAuthenticationMechanism() != null)
      {
         if (registration.getAuthenticationMechanism().getType() instanceof BasicAuth)
         {
            BasicAuth basic = (BasicAuth) registration.getAuthenticationMechanism().getType();
            client.getState().setCredentials(
                    //new AuthScope(null, 8080, "Test"),
                    new AuthScope(AuthScope.ANY),
                    new UsernamePasswordCredentials(basic.getUsername(), basic.getPassword())
            );
            client.getParams().setAuthenticationPreemptive(true);
            authenticated = true;
         }
      }
      nextPost = registration.getTarget().getDelegate();
      if (nextPost == null)
      {
         throw new RuntimeException("registration link cannot be null.  Don't know how to forward messages");
      }
      method = registration.getTarget().getMethod();
      if (method == null) method = "POST";
      contentType = registration.getTarget().getType();

      session = factory.createSession(false, false);
      consumer = session.createConsumer(destination);
      consumer.setMessageHandler(this);
   }

   public void stop()
   {
      try
      {
         consumer.close();
      }
      catch (HornetQException e)
      {
      }
      try
      {
         session.close();
      }
      catch (HornetQException e)
      {

      }
   }

   @Override
   public void onMessage(ClientMessage clientMessage)
   {
      Link next = nextPost;
      String httpMethod = method;

      try
      {
         push(clientMessage, next, httpMethod);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   protected void push(ClientMessage clientMessage, Link next, String httpMethod) throws Exception
   {
      for (int i = 0; i < 3; i++)
      {
         int wait = 0;
         ClientRequest request = next.request(executor);
         request.followRedirects(false);

         for (XmlHttpHeader header : registration.getHeaders())
         {
            request.header(header.getName(), header.getValue());
         }

         HttpMessage.buildMessage(clientMessage, request, contentType);

         ClientResponse response = request.httpMethod(httpMethod);
         if ((response.getStatus() >= 200 && response.getStatus() < 299) || response.getStatus() == 303 || response.getStatus() == 304)
         {
            if (response.getLinkHeader() != null)
            {
               Link createNext = response.getLinkHeader().getLinkByTitle("create-next");
               if (createNext != null)
               {
                  nextPost = createNext;
                  method = "PUT";
               }
            }
            // If we crash here we're really f'cked
            // gotta make a 2pc protocol here
            clientMessage.acknowledge();
            return;
         }
         else if (response.getStatus() == 307)
         {
            Link location = response.getLocation();
            if (location == null)
            {
               throw new RuntimeException("307 redirect has no location header to redirect");
            }
            next = location;
         }
         else if (response.getStatus() == Response.Status.SERVICE_UNAVAILABLE.getStatusCode())
         {
            String retryAfter = (String) response.getHeaders().getFirst("Retry-After");
            if (retryAfter != null)
            {
               wait = Integer.parseInt(retryAfter);
            }
         }
         else
         {
            throw new RuntimeException("failed to push message to: " + next + " status code: " + response.getStatus());
         }
         if (wait == 0) wait = i + 1;
         Thread.sleep(wait * 1000);
      }
   }
}
