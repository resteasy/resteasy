package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.LinkHeaderSupport;
import org.jboss.resteasy.star.messaging.queue.ConsumerFactory;
import org.jboss.resteasy.star.messaging.queue.QueueConsumer;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.ConcurrentHashMap;
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
   protected ConsumerFactory consumerFactory;

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

   public ConsumerFactory getConsumerFactory()
   {
      return consumerFactory;
   }

   public void setConsumerFactory(ConsumerFactory consumerFactory)
   {
      this.consumerFactory = consumerFactory;
   }

   protected String generateSubscriptionName()
   {
      return startup + "-" + sessionCounter.getAndIncrement();
   }

   @POST
   public Response createSubscription(@FormParam("durable") @DefaultValue("false") boolean durable,
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
         QueueConsumer consumer = consumerFactory.createConsumer(subscriptionName, sessionFactory, subscriptionName);
         queueConsumers.put(consumer.getId(), consumer);
         Subscription subscription = (Subscription) consumer;
         subscription.setDurable(durable);
         UriBuilder location = uriInfo.getAbsolutePathBuilder();
         location.path(consumer.getId());
         Response.ResponseBuilder builder = Response.created(location.build());
         SubscriptionResource.setConsumeNextLink(builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/" + consumer.getId());
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

   @Path("{subscription-id}")
   public QueueConsumer getSubscription(
           @PathParam("subscription-id") String subscriptionId)
   {
      QueueConsumer consumer = queueConsumers.get(subscriptionId);
      if (consumer == null)
      {
         ClientSession session = null;
         try
         {
            session = sessionFactory.createSession();

            ClientSession.QueueQuery query = session.queueQuery(new SimpleString(subscriptionId));
            if (query.isExists())
            {

               consumer = consumerFactory.createConsumer(subscriptionId, sessionFactory, subscriptionId);
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
      }
      return consumer;
   }


   @Path("{subscription-id}")
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