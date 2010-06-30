package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.queue.QueueConsumer;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SubscriptionsResource
{
   protected ConcurrentHashMap<String, QueueConsumer> queueConsumers = new ConcurrentHashMap<String, QueueConsumer>();
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected final String startup = Long.toString(System.currentTimeMillis());
   protected AtomicLong sessionCounter = new AtomicLong(1);
   protected ExecutorService ackTimeoutService;
   protected long ackTimeoutSeconds;

   public ExecutorService getAckTimeoutService()
   {
      return ackTimeoutService;
   }

   public void setAckTimeoutService(ExecutorService ackTimeoutService)
   {
      this.ackTimeoutService = ackTimeoutService;
   }

   public long getAckTimeoutSeconds()
   {
      return ackTimeoutSeconds;
   }

   public void setAckTimeoutSeconds(long ackTimeoutSeconds)
   {
      this.ackTimeoutSeconds = ackTimeoutSeconds;
   }

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   public void stop()
   {
      for (QueueConsumer consumer : queueConsumers.values())
      {
         consumer.shutdown();
         Subscription subscription = (Subscription) consumer;
         if (!subscription.isDurable())
         {
            deleteSubscriberQueue(consumer);
         }

      }
   }

   protected String generateSubscriptionName()
   {
      return startup + "-" + sessionCounter.getAndIncrement();
   }

   @POST
   public Response createSubscription(@FormParam("durable") @DefaultValue("false") boolean durable,
                                      @FormParam("autoAck") @DefaultValue("true") boolean autoAck,
                                      @Context UriInfo uriInfo)
   {
      String subscriptionName = generateSubscriptionName();
      ClientSession session = null;
      try
      {
         session = sessionFactory.createSession();

         if (durable)
         {
            session.createQueue(destination, subscriptionName, true);
         }
         else
         {
            session.createTemporaryQueue(destination, subscriptionName);
         }
         QueueConsumer consumer = createConsumer(durable, autoAck, subscriptionName);
         queueConsumers.put(consumer.getId(), consumer);

         UriBuilder location = uriInfo.getAbsolutePathBuilder();
         if (autoAck) location.path("auto-ack");
         else location.path("acknowledged");
         location.path(consumer.getId());
         Response.ResponseBuilder builder = Response.created(location.build());
         if (autoAck)
         {
            SubscriptionResource.setConsumeNextLink(builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/auto-ack/" + consumer.getId());
         }
         else
         {
            AcknowledgedSubscriptionResource.setAcknowledgeNextLink(builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/acknowledged/" + consumer.getId());

         }
         return builder.build();

      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         if (session != null)
         {
            try
            {
               session.close();
            }
            catch (HornetQException e)
            {
            }
         }
      }
   }

   protected QueueConsumer createConsumer(boolean durable, boolean autoAck, String subscriptionName)
           throws HornetQException
   {
      QueueConsumer consumer;
      if (autoAck)
      {
         SubscriptionResource subscription = new SubscriptionResource(sessionFactory, subscriptionName, subscriptionName);
         subscription.setDurable(durable);
         consumer = subscription;
      }
      else
      {
         AcknowledgedSubscriptionResource subscription = new AcknowledgedSubscriptionResource(sessionFactory, subscriptionName, subscriptionName, this.ackTimeoutService, this.ackTimeoutSeconds);
         subscription.setDurable(durable);
         consumer = subscription;
      }
      return consumer;
   }

   @Path("auto-ack/{subscription-id}")
   public QueueConsumer getAutoAckSubscription(
           @PathParam("subscription-id") String subscriptionId)
   {
      QueueConsumer consumer = queueConsumers.get(subscriptionId);
      if (consumer == null)
      {
         consumer = recreateQueueConsumer(subscriptionId, true);
      }
      return consumer;
   }

   @Path("acknowledged/{subscription-id}")
   public QueueConsumer getAcknoledgeSubscription(
           @PathParam("subscription-id") String subscriptionId)
   {
      QueueConsumer consumer = queueConsumers.get(subscriptionId);
      if (consumer == null)
      {
         consumer = recreateQueueConsumer(subscriptionId, false);
      }
      return consumer;
   }

   private QueueConsumer recreateQueueConsumer(String subscriptionId, boolean autoAck)
   {
      QueueConsumer consumer;
      ClientSession session = null;
      try
      {
         session = sessionFactory.createSession();

         ClientSession.QueueQuery query = session.queueQuery(new SimpleString(subscriptionId));
         if (query.isExists())
         {

            consumer = createConsumer(true, autoAck, subscriptionId);
            queueConsumers.put(consumer.getId(), consumer);
         }
         else
         {
            throw new WebApplicationException(Response.serverError()
                    .entity("Failed to match a subscriber to URL" + subscriptionId)
                    .type("text/plain").build());
         }
      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         if (session != null)
         {
            try
            {
               session.close();
            }
            catch (HornetQException e)
            {
            }
         }
      }
      return consumer;
   }


   @Path("acknowledged/{subscription-id}")
   @DELETE
   public void deleteAckSubscription(
           @PathParam("subscription-id") String consumerId)
   {
      deleteSubscription(consumerId);
   }

   @Path("auto-ack/{subscription-id}")
   @DELETE
   public void deleteSubscription(
           @PathParam("subscription-id") String consumerId)
   {
      QueueConsumer consumer = queueConsumers.remove(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                 .entity("Failed to match a subscription to URL" + consumerId)
                 .type("text/plain").build());
      }
      consumer.shutdown();
      deleteSubscriberQueue(consumer);

   }

   private void deleteSubscriberQueue(QueueConsumer consumer)
   {
      String subscriptionName = consumer.getId();
      ClientSession session = null;
      try
      {
         session = sessionFactory.createSession();

         session.deleteQueue(subscriptionName);
      }
      catch (HornetQException e)
      {
      }
      finally
      {
         if (session != null)
         {
            try
            {
               session.close();
            }
            catch (HornetQException e)
            {
            }
         }
      }
   }
}