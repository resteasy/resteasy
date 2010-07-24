package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.jboss.resteasy.star.messaging.util.LinkStrategy;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
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
      autoAck = false;
   }

   @Path("acknowledge-next")
   @POST
   public synchronized Response poll(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                                     @MatrixParam("index") long index,
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
      return checkIndexAndPoll(wait, info, info.getMatchedURIs().get(1), index);
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
         setAcknowledgeLinks(uriInfo, basePath, builder, "-1");
         return builder.build();
      }

      if (ack == null || !ack.getAckToken().equals(ackToken))
      {
         Response.ResponseBuilder builder = Response.status(Response.Status.PRECONDITION_FAILED)
                 .entity("Could not acknowledge message, it was probably requeued from a timeout or you have an old link")
                 .type("text/plain");
         setAcknowledgeLinks(uriInfo, basePath, builder, "-1");
         return builder.build();
      }
      
      // clear indexes as we know the client got the message and won't send a duplicate ack-next
      previousIndex = -2;
      lastConsumed = null;

      if (ack.wasSet() && doAcknowledge != ack.isAcknowledged())
      {
         StringBuilder msg = new StringBuilder("Could not ");
         if (doAcknowledge == false) msg.append("un");
         msg.append("acknowledge message because it has already been ");
         if (doAcknowledge == true) msg.append("un");
         msg.append("acknowledged");

         Response.ResponseBuilder builder = Response.status(Response.Status.PRECONDITION_FAILED)
                 .entity(msg.toString())
                 .type("text/plain");
         setAcknowledgeLinks(uriInfo, basePath, builder, "-1");
         return builder.build();
      }

      if (ack.wasSet() && doAcknowledge == ack.isAcknowledged())
      {
         Response.ResponseBuilder builder = Response.noContent();
         setAcknowledgeLinks(uriInfo, basePath, builder, "-1");
         return builder.build();
      }

      if (doAcknowledge)
      {
         try
         {
            ack.acknowledge();
            System.out.println("Acknowledge message: " + ack.getMessage());
            ack.getMessage().acknowledge();
         }
         catch (HornetQException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         ack.unacknowledge();
         unacknowledge();
      }
      Response.ResponseBuilder builder = Response.noContent();
      setAcknowledgeLinks(uriInfo, basePath, builder, "-1");
      return builder.build();
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

   protected void unacknowledge()
   {
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


   protected void setAcknowledgeLinks(UriInfo uriInfo, String basePath, Response.ResponseBuilder builder, String index)
   {
      setAcknowledgeNextLink(serviceManager.getLinkStrategy(), builder, uriInfo, basePath, index);
      setSessionLink(builder, uriInfo, basePath);
   }


   @Override
   protected void setMessageResponseLinks(UriInfo info, String basePath, Response.ResponseBuilder builder, String index)
   {
      setAcknowledgementLink(builder, info, basePath);
      setSessionLink(builder, info, basePath);
   }

   @Override
   protected void setPollTimeoutLinks(UriInfo info, String basePath, Response.ResponseBuilder builder, String index)
   {
      setAcknowledgeNextLink(serviceManager.getLinkStrategy(), builder, info, basePath, index);
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

   public static void setAcknowledgeNextLink(LinkStrategy linkStrategy, Response.ResponseBuilder response, UriInfo info, String basePath, String index)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath)
              .path("acknowledge-next");
      if (index != null)
      {
         builder.matrixParam("index", index);
      }
      String uri = builder.build().toString();
      linkStrategy.setLinkHeader(response, "acknowledge-next", "acknowledge-next", uri, MediaType.APPLICATION_FORM_URLENCODED);
   }


}
