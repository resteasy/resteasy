package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.jboss.resteasy.star.messaging.util.LinkStrategy;

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
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcknowledgedQueueConsumer extends QueueConsumer
{
   protected long counter;
   protected String startup = Long.toString(System.currentTimeMillis());
   protected volatile Acknowledgement ack;

   public AcknowledgedQueueConsumer(ClientSessionFactory factory, String destination, String id, DestinationServiceManager serviceManager)
           throws HornetQException
   {
      super(factory, destination, id, serviceManager);
   }

   @Path("acknowledge-next")
   @POST
   public synchronized Response poll(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                                     @Context UriInfo info)
   {
      if (closed)
      {
         UriBuilder builder = info.getBaseUriBuilder();
         String path = info.getMatchedURIs().get(1);
         builder.path(path)
                 .path("acknowledge-next");
         String uri = builder.build().toString();

         // redirect to another acknowledge-next

         return Response.status(307).location(URI.create(uri)).build();
      }
      return runPoll(wait, info, info.getMatchedURIs().get(1));
   }


   @Override
   public synchronized void shutdown()
   {
      super.shutdown();
      if (ack != null)
      {
         ack = null;
      }
   }


   @Path("acknowledgement/{ackToken}/next")
   @POST
   public synchronized Response acknowledgeAndNext(
           @HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
           @PathParam("ackToken") String ackToken,
           @Context UriInfo uriInfo)
   {
      ping();
      String basePath = uriInfo.getMatchedURIs().get(1);
      if (closed)
      {
         return failAcknowledgement(uriInfo, basePath);
      }
      boolean acknowledged = acknowledge(ackToken);
      if (!acknowledged)
      {
         return failAcknowledgement(uriInfo, basePath);
      }
      return runPoll(wait, uriInfo, basePath);

   }

   private Response failAcknowledgement(UriInfo uriInfo, String basePath)
   {
      Response.ResponseBuilder builder = Response.status(Response.Status.PRECONDITION_FAILED)
              .entity("Could not acknowledge previous message, it was probably requeued from a timeout")
              .type("text/plain");
      setAcknowledgeLinks(uriInfo, basePath, builder);
      return builder.build();
   }

   @Path("acknowledgement/{ackToken}")
   @POST
   public synchronized Response acknowledge(
           @PathParam("ackToken") String ackToken,
           @FormParam("acknowledge") boolean doAcknowledge,
           @Context UriInfo uriInfo)
   {
      ping();
      String basePath = uriInfo.getMatchedURIs().get(1);
      if (closed)
      {
         Response.ResponseBuilder builder = Response.status(Response.Status.PRECONDITION_FAILED)
                 .entity("Could not acknowledge message, it was probably requeued from a timeout")
                 .type("text/plain");
         setAcknowledgeLinks(uriInfo, basePath, builder);
         return builder.build();
      }
      if (!doAcknowledge)
      {
         unacknowledge(ackToken);
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
                 .entity("Could not acknowledge message, it was probably requeued from a timeout")
                 .type("text/plain");
         setAcknowledgeLinks(uriInfo, basePath, builder);
         return builder.build();
      }
   }

   @Override
   protected void createSession(ClientSessionFactory factory, String destination) throws HornetQException
   {
      session = factory.createSession();
      consumer = session.createConsumer(destination);
      session.start();
   }

   @Override
   protected ClientMessage receive(long timeoutSecs) throws Exception
   {
      ClientMessage msg = super.receive(timeoutSecs);
      return msg;
   }

   @Override
   protected ClientMessage receiveFromConsumer(long timeoutSecs) throws Exception
   {
      ClientMessage message = super.receiveFromConsumer(timeoutSecs);
      if (message != null)
      {
         ack = new Acknowledgement((counter++) + startup, message);
         //System.out.println("---> Setting ack: " + ack.getAckToken());
      }
      return message;
   }


   protected String getAckToken()
   {
      return ack.getAckToken();
   }

   protected boolean acknowledge(String ackToken)
   {

      if (ack == null) return false;
      if (!ack.getAckToken().equals(ackToken))
      {
         return false;
      }
      try
      {
         ack.getMessage().acknowledge();
      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
      return true;
   }

   protected void unacknowledge(String ackToken)
   {

      if (ack == null) return;
      if (!ack.getAckToken().equals(ackToken))
      {
         return;
      }

      // close session so message gets redelivered

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
         throw new RuntimeException();

      }
   }


   protected void setAcknowledgeLinks(UriInfo uriInfo, String basePath, Response.ResponseBuilder builder)
   {
      setAcknowledgeNextLink(serviceManager.getLinkStrategy(), builder, uriInfo, basePath);
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
      setAcknowledgeNextLink(serviceManager.getLinkStrategy(), builder, info, basePath);
      setSessionLink(builder, info, basePath);
   }

   protected void setAcknowledgementLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledgement")
              .path(getAckToken());
      String uri = builder.build().toString();
      serviceManager.getLinkStrategy().setLinkHeader(response, "acknowledgement", "acknowledgement", uri, MediaType.APPLICATION_FORM_URLENCODED);
   }

   protected void setAcknowledgementAndNextLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledgement")
              .path(getAckToken())
              .path("next");
      String uri = builder.build().toString();
      serviceManager.getLinkStrategy().setLinkHeader(response, "acknowledge-next", "acknowledge-next", uri, MediaType.APPLICATION_FORM_URLENCODED);
   }

   public static void setAcknowledgeNextLink(LinkStrategy linkStrategy, Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledge-next");
      String uri = builder.build().toString();
      linkStrategy.setLinkHeader(response, "acknowledge-next", "acknowledge-next", uri, MediaType.APPLICATION_FORM_URLENCODED);
   }


}
