package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.Constants;
import org.jboss.resteasy.star.messaging.HttpMessage;
import org.jboss.resteasy.star.messaging.LinkHeaderSupport;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueConsumer
{
   protected ClientSessionFactory factory;
   protected ClientSession session;
   protected ClientConsumer consumer;
   protected String destination;
   protected boolean isClosed;
   protected String id;

   public QueueConsumer(ClientSessionFactory factory, String destination, String id) throws HornetQException
   {
      this.factory = factory;
      this.destination = destination;
      this.id = id;

      createSession(factory, destination);
   }

   protected void createSession(ClientSessionFactory factory, String destination)
           throws HornetQException
   {
      session = factory.createSession(true, true);
      consumer = session.createConsumer(destination);
      session.start();
   }

   protected synchronized ClientMessage receiveFromConsumer(long timeoutSecs) throws Exception
   {
      if (timeoutSecs <= 0)
      {
         return consumer.receiveImmediate();
      }
      else
      {
         return consumer.receive(timeoutSecs * 1000);
      }

   }

   protected synchronized void failedToUnmarshallMessage(Exception ex)
   {
      System.err.println("Failed to unmarshall message.  Acknowledging and ignoring");
      ex.printStackTrace();
   }

   public synchronized ClientMessage receive(long timeoutSecs) throws Exception
   {
      System.out.println("receive in consumer: " + id);
      return receiveFromConsumer(timeoutSecs);
   }

   public String getId()
   {
      return id;
   }

   public synchronized void shutdown()
   {
      isClosed = true;
      try
      {
         consumer.close();
      }
      catch (Exception e)
      {
      }

      try
      {
         session.close();
      }
      catch (Exception e)
      {
      }
      session = null;
      consumer = null;
   }


   @Path("consume-next")
   @POST
   public Response poll(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                        @Context UriInfo info)
   {
      return runPoll(wait, info, info.getMatchedURIs().get(1));
   }

   public Response runPoll(long wait, UriInfo info, String basePath)
   {
      try
      {
         synchronized (this)
         {
            ClientMessage message = receive(wait);
            if (message == null)
            {
               System.out.println("Timed out waiting for message receive.");
               Response.ResponseBuilder builder = Response.status(503).entity("Timed out waiting for message receive.").type("text/plain");
               setSessionLink(builder, info, basePath);
               setConsumeNextLink(builder, info, basePath);
               return builder.build();
            }
            return getMessageResponse(message, info, basePath).build();
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected Response.ResponseBuilder getMessageResponse(ClientMessage msg, UriInfo info, String basePath)
   {
      Response.ResponseBuilder responseBuilder = Response.ok();
      setMessageResponseLinks(info, basePath, responseBuilder);
      HttpMessage.buildMessage(msg, responseBuilder);
      return responseBuilder;
   }

   protected void setMessageResponseLinks(UriInfo info, String basePath, Response.ResponseBuilder responseBuilder)
   {
      setConsumeNextLink(responseBuilder, info, basePath);
      setSessionLink(responseBuilder, info, basePath);
   }

   public static void setConsumeNextLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("consume-next");
      String uri = builder.build().toString();
      Link link = new Link("consume-next", "consume-next", uri, MediaType.APPLICATION_FORM_URLENCODED, null);
      LinkHeaderSupport.setLinkHeader(response, link);
   }

   protected void setSessionLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      String uri = builder.build().toString();
      Link link = new Link("session", "session", uri, MediaType.APPLICATION_XML, null);
      LinkHeaderSupport.setLinkHeader(response, link);
   }
}