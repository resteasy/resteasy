package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.jboss.resteasy.star.messaging.util.LinkHeaderSupport;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcknowledgedQueueConsumer extends QueueConsumer implements Runnable
{
   protected long counter;
   protected String startup = Long.toString(System.currentTimeMillis());
   protected ExecutorService ackTimeoutService;
   protected long ackTimeoutSeconds;
   protected volatile AckStateMachine ack;
   protected volatile Future currentAckTask;

   public AcknowledgedQueueConsumer(ClientSessionFactory factory, String destination, String id, ExecutorService ackTimeoutService, long ackTimeoutSeconds)
           throws HornetQException
   {
      super(factory, destination, id);
      this.ackTimeoutService = ackTimeoutService;
      this.ackTimeoutSeconds = ackTimeoutSeconds;
   }

   @Override
   protected void createSession(ClientSessionFactory factory, String destination) throws HornetQException
   {
      session = factory.createSession();
      consumer = session.createConsumer(destination);
      session.start();
   }

   @Override
   public synchronized ClientMessage receive(long timeoutSecs) throws Exception
   {
      ClientMessage msg = super.receive(timeoutSecs);
      if (msg != null)
      {
         currentAckTask = ackTimeoutService.submit(this);
      }
      return msg;
   }

   @Override
   protected synchronized ClientMessage receiveFromConsumer(long timeoutSecs) throws Exception
   {
      ClientMessage message = super.receiveFromConsumer(timeoutSecs);
      if (message != null)
      {
         ack = new AckStateMachine((counter++) + startup, message);
         System.out.println("---> Setting ack: " + ack.getAckToken());
      }
      return message;
   }

   @Path("acknowledge-next")
   @POST
   public Response poll(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                        @Context UriInfo info)
   {
      return runPoll(wait, info, info.getMatchedURIs().get(1));
   }


   public String getAckToken()
   {
      return ack.getAckToken();
   }

   public boolean acknowledge(String ackToken)
   {
      AckStateMachine tmpAck = null;
      synchronized (this)
      {
         tmpAck = ack;
         if (tmpAck == null)
         {
            return false;
         }
      }
      return tmpAck.acknowledgeSend(ackToken);
   }

   public void unacknowledge()
   {
      AckStateMachine tmpAck = null;
      synchronized (this)
      {
         tmpAck = ack;
      }
      tmpAck.unacknowledgeSend();
   }

   private void reset()
   {
      System.out.println("reseting consumer");
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

      consumer = null;
      session = null;

      try
      {
         session = factory.createSession();
         consumer = session.createConsumer(destination);
         session.start();
      }
      catch (Exception e)
      {
         shutdown();
      }
   }

   @Override
   public synchronized void shutdown()
   {
      super.shutdown();
      if (ack != null)
      {
         ack.unacknowledgeSend();
         ack = null;
      }
      if (currentAckTask != null)
      {
         currentAckTask.cancel(true);
         currentAckTask = null;
      }


   }


   @Override
   public void run()
   {
      System.out.println("------ starting ack timeout task -----");
      try
      {
         AckStateMachine tmpAck = null;
         synchronized (this)
         {
            tmpAck = ack;
         }
         if (tmpAck == null) return;
         boolean obtained = tmpAck.getAck().await(ackTimeoutSeconds, TimeUnit.SECONDS);
         synchronized (this)
         {
            if (isClosed)
            {
               System.out.println("invalidating because its old");
               tmpAck.invalidate();
               return;
            }
            if (obtained == false)
            {
               System.out.println("invalidating because timeout");
               reset();
               tmpAck.invalidate();
            }
            else
            {
               if (tmpAck.isUnacknowledged())
               {
                  reset();
                  tmpAck.invalidate();
               }
               else
               {
                  try
                  {
                     System.out.println("acknowledging message");
                     tmpAck.getMessage().acknowledge();
                     tmpAck.acknowledge();
                  }
                  catch (HornetQException e)
                  {
                     System.out.println("Unable to acknowledge message");
                     tmpAck.invalidate();
                     reset();
                  }

               }
            }
            currentAckTask = null;
            ack = null;
         }
      }
      catch (Exception e)
      {
         currentAckTask = null;
         System.out.println("Ack timeout task interuppted");
      }
      finally
      {
         System.out.println("------ Ending ack timeout task -----");
      }


   }

   @Path("acknowledgement/{ackToken}/next")
   @POST
   public Response acknowledgeAndNext(
           @HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
           @PathParam("ackToken") String ackToken,
           @Context UriInfo uriInfo)
   {
      boolean acknowledged = acknowledge(ackToken);
      String basePath = uriInfo.getMatchedURIs().get(1);
      if (!acknowledged)
      {
         Response.ResponseBuilder builder = Response.status(Response.Status.PRECONDITION_FAILED)
                 .entity("Requeued before acknowledgement")
                 .type("text/plain");
         setAcknowledgeNextLink(builder, uriInfo, basePath);
         return builder.build();
      }
      return runPoll(wait, uriInfo, basePath);

   }

   @Path("acknowledgement/{ackToken}")
   @POST
   public Response acknowledge(
           @PathParam("ackToken") String ackToken,
           @FormParam("acknowledge") boolean doAcknowledge,
           @Context UriInfo uriInfo)
   {
      System.out.println("acknowledge: " + ackToken);
      if (ack != null) System.out.println("current token: " + ack.getAckToken());
      String basePath = uriInfo.getMatchedURIs().get(1);
      if (!doAcknowledge)
      {
         unacknowledge();
         Response.ResponseBuilder builder = Response.noContent();
         setAcknowledgeLinks(uriInfo, basePath, builder);
         return builder.build();
      }
      boolean acknowledged = acknowledge(ackToken);
      if (acknowledged)
      {
         Response.ResponseBuilder builder = Response.noContent();
         setAcknowledgeLinks(uriInfo, basePath, builder);
         return builder.build();
      }
      else
      {
         Response.ResponseBuilder builder = Response.status(Response.Status.PRECONDITION_FAILED)
                 .entity("Requeued before acknowledgement")
                 .type("text/plain");
         setAcknowledgeLinks(uriInfo, basePath, builder);
         return builder.build();
      }
   }

   protected void setAcknowledgeLinks(UriInfo uriInfo, String basePath, Response.ResponseBuilder builder)
   {
      setAcknowledgeNextLink(builder, uriInfo, basePath);
      setSessionLink(builder, uriInfo, basePath);
   }


   @Override
   protected void setMessageResponseLinks(UriInfo info, String basePath, Response.ResponseBuilder builder)
   {
      setAcknowledgementLink(builder, info, basePath);
      setAcknowledgementAndNextLink(builder, info, basePath);
      setSessionLink(builder, info, basePath);
   }

   @Override
   protected void setPollTimeoutLinks(UriInfo info, String basePath, Response.ResponseBuilder builder)
   {
      setAcknowledgeNextLink(builder, info, basePath);
      setSessionLink(builder, info, basePath);
   }

   protected void setAcknowledgementLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledgement")
              .path(getAckToken());
      String uri = builder.build().toString();
      Link link = new Link("acknowledgement", "acknowledgement", uri, MediaType.APPLICATION_FORM_URLENCODED, null);
      LinkHeaderSupport.setLinkHeader(response, link);
   }

   protected void setAcknowledgementAndNextLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledgement")
              .path(getAckToken())
              .path("next");
      String uri = builder.build().toString();
      Link link = new Link("acknowledge-next", "acknowledge-next", uri, MediaType.APPLICATION_FORM_URLENCODED, null);
      LinkHeaderSupport.setLinkHeader(response, link);
   }

   public static void setAcknowledgeNextLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledge-next");
      String uri = builder.build().toString();
      Link link = new Link("acknowledge-next", "acknowledge-next", uri, MediaType.APPLICATION_FORM_URLENCODED, null);
      LinkHeaderSupport.setLinkHeader(response, link);
   }


}
