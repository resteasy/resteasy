package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.queue.DestinationServiceManager;
import org.jboss.resteasy.star.messaging.queue.QueueConsumer;
import org.jboss.resteasy.star.messaging.util.TimeoutTask;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SubscriptionsResource implements TimeoutTask.Callback
{
   protected ConcurrentHashMap<String, QueueConsumer> queueConsumers = new ConcurrentHashMap<String, QueueConsumer>();
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected final String startup = Long.toString(System.currentTimeMillis());
   protected AtomicLong sessionCounter = new AtomicLong(1);
   protected int consumerTimeoutSeconds;
   protected DestinationServiceManager serviceManager;

   public DestinationServiceManager getServiceManager()
   {
      return serviceManager;
   }

   public void setServiceManager(DestinationServiceManager serviceManager)
   {
      this.serviceManager = serviceManager;
   }

   public int getConsumerTimeoutSeconds()
   {
      return consumerTimeoutSeconds;
   }

   public void setConsumerTimeoutSeconds(int consumerTimeoutSeconds)
   {
      this.consumerTimeoutSeconds = consumerTimeoutSeconds;
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

   private Object timeoutLock = new Object();

   @Override
   public void testTimeout(String target)
   {
      synchronized (timeoutLock)
      {
         QueueConsumer consumer = queueConsumers.get(target);
         if (consumer == null) return;
         synchronized (consumer)
         {
            if (System.currentTimeMillis() - consumer.getLastPingTime() > consumerTimeoutSeconds * 1000)
            {
               System.out.println("**** shutdown because of session timeout for: " + consumer.getId());
               consumer.shutdown();
               queueConsumers.remove(consumer.getId());
               serviceManager.getTimeoutTask().remove(consumer.getId());
            }
         }
      }
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
         serviceManager.getTimeoutTask().add(this, consumer.getId());

         UriBuilder location = uriInfo.getAbsolutePathBuilder();
         if (autoAck) location.path("auto-ack");
         else location.path("acknowledged");
         location.path(consumer.getId());
         Response.ResponseBuilder builder = Response.created(location.build());
         if (autoAck)
         {
            SubscriptionResource.setConsumeNextLink(serviceManager.getLinkStrategy(), builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/auto-ack/" + consumer.getId());
         }
         else
         {
            AcknowledgedSubscriptionResource.setAcknowledgeNextLink(serviceManager.getLinkStrategy(), builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/acknowledged/" + consumer.getId());

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
         SubscriptionResource subscription = new SubscriptionResource(sessionFactory, subscriptionName, subscriptionName, serviceManager);
         subscription.setDurable(durable);
         consumer = subscription;
      }
      else
      {
         AcknowledgedSubscriptionResource subscription = new AcknowledgedSubscriptionResource(sessionFactory, subscriptionName, subscriptionName, serviceManager);
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
         consumer = recreateTopicConsumer(subscriptionId, true);
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
         consumer = recreateTopicConsumer(subscriptionId, false);
      }
      return consumer;
   }

   private QueueConsumer recreateTopicConsumer(String subscriptionId, boolean autoAck)
   {
      QueueConsumer consumer;
      ClientSession session = null;
      try
      {
         session = sessionFactory.createSession();

         ClientSession.QueueQuery query = session.queueQuery(new SimpleString(subscriptionId));
         if (query.isExists())
         {
            synchronized (timeoutLock)
            {
               QueueConsumer tmp = createConsumer(true, autoAck, subscriptionId);
               consumer = queueConsumers.putIfAbsent(subscriptionId, tmp);
               if (consumer == null)
               {
                  consumer = tmp;
                  serviceManager.getTimeoutTask().add(this, subscriptionId);
               }
               else
               {
                  tmp.shutdown();
               }
            }
         }
         else
         {
            throw new WebApplicationException(Response.status(405)
                    .entity("Failed to find subscriber " + subscriptionId + " you will have to reconnect")
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
         String msg = "Failed to match a subscription to URL " + consumerId;
         System.out.println(msg);
         throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                 .entity(msg)
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