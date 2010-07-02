package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.jboss.resteasy.star.messaging.queue.push.PushConsumerResource;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.jboss.resteasy.star.messaging.util.LinkHeaderSupport;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueResource
{
   protected String destination;
   protected PostMessage sender;
   protected ConsumersResource consumers;
   protected PushConsumerResource pushConsumers;

   public void start() throws Exception
   {
   }

   public void stop()
   {
      consumers.stop();
      pushConsumers.stop();
      sender.cleanup();
   }

   public PostMessage getSender()
   {
      return sender;
   }

   public void setSender(PostMessage sender)
   {
      this.sender = sender;
   }

   @GET
   @Produces("application/xml")
   public Response get(@Context UriInfo uriInfo)
   {


      String msg = "<queue>"
              + "<name>" + destination + "</name>"
              + "</queue/>";
      Response.ResponseBuilder builder = Response.ok(msg);
      setSenderLink(builder, uriInfo);
      setConsumeNextLink(builder, uriInfo);
      setAcknowledgeNextLink(builder, uriInfo);
      setPushSubscriptionsLink(builder, uriInfo);
      return builder.build();
   }

   @HEAD
   @Produces("application/xml")
   public Response head(@Context UriInfo uriInfo)
   {
      Response.ResponseBuilder builder = Response.ok();
      setSenderLink(builder, uriInfo);
      setConsumeNextLink(builder, uriInfo);
      setAcknowledgeNextLink(builder, uriInfo);
      setPushSubscriptionsLink(builder, uriInfo);
      return builder.build();
   }

   protected void setSenderLink(Response.ResponseBuilder response, UriInfo info)
   {
      UriBuilder builder = info.getRequestUriBuilder();
      builder.path("create");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "create", "create", uri, null);
   }

   protected void setConsumeNextLink(Response.ResponseBuilder response, UriInfo info)
   {
      UriBuilder builder = info.getRequestUriBuilder();
      builder.path("consume-next");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "consume-next", "consume-next", uri, null);
   }

   protected void setAcknowledgeNextLink(Response.ResponseBuilder response, UriInfo info)
   {
      UriBuilder builder = info.getRequestUriBuilder();
      builder.path("acknowledge-next");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "acknowledge-next", "acknowledge-next", uri, null);
   }


   protected void setPushSubscriptionsLink(Response.ResponseBuilder response, UriInfo info)
   {
      UriBuilder builder = info.getRequestUriBuilder();
      builder.path("push-subscriptions");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "push-subscriptions", "push-subscriptions", uri, null);
   }


   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   public void setConsumers(ConsumersResource consumers)
   {
      this.consumers = consumers;
   }

   @Path("create")
   public Object post() throws Exception
   {
      return sender;
   }


   @Path("acknowledge-next")
   @POST
   public Response acknowledgeNext(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                                   @Context UriInfo info)
   {
      try
      {
         for (int i = 0; i < 5; i++)
         {
            Response response = doAcknowledgeNext(wait, info);
            if (response != null) return response;
         }
         return Response.serverError().type("text/plain").entity("Closed session, could not retry").build();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private Response doAcknowledgeNext(long wait, UriInfo info) throws Exception
   {
      QueueConsumer consumer = consumers.createAcknowledgedConsumer();
      if (!consumer.isClosed())
      {
         String basePath = info.getMatchedURIs().get(1) + "/consumers/acknowledged/" + consumer.getId();
         return consumer.runPoll(wait, info, basePath);
      }
      else
      {
         return null;
      }
   }

   @Path("consume-next")
   @POST
   public Response consumeNext(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                               @Context UriInfo info)
   {
      try
      {
         for (int i = 0; i < 5; i++)
         {
            Response response = doConsumeNext(wait, info);
            if (response != null) return response;
         }
         return Response.serverError().type("text/plain").entity("Closed session, could not retry").build();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private Response doConsumeNext(long wait, UriInfo info)
           throws HornetQException
   {
      QueueConsumer consumer = consumers.createConsumer();
      synchronized (consumer)
      {
         if (!consumer.isClosed())
         {
            String basePath = info.getMatchedURIs().get(1) + "/consumers/auto-ack/" + consumer.getId();
            return consumer.runPoll(wait, info, basePath);
         }
         else
         {
            return null;
         }
      }
   }

   @Path("consumers")
   public ConsumersResource getConsumers()
   {
      return consumers;
   }

   public void setPushConsumers(PushConsumerResource pushConsumers)
   {
      this.pushConsumers = pushConsumers;
   }

   @Path("push-subscriptions")
   public PushConsumerResource getPushConsumers()
   {
      return pushConsumers;
   }
}