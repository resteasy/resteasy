package org.hornetq.rest.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.hornetq.rest.queue.push.PushConsumer;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushSubscriptionsResource
{
   protected Map<String, PushConsumer> consumers = new ConcurrentHashMap<String, PushConsumer>();
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected final String startup = Long.toString(System.currentTimeMillis());
   protected final AtomicLong sessionCounter = new AtomicLong(1);
   protected TopicPushStore pushStore;

   public void stop()
   {
      for (PushConsumer consumer : consumers.values())
      {
         consumer.stop();
         if (consumer.getRegistration().isDurable() == false)
         {
            deleteSubscriberQueue(consumer);
         }
      }
   }

   public TopicPushStore getPushStore()
   {
      return pushStore;
   }

   public void setPushStore(TopicPushStore pushStore)
   {
      this.pushStore = pushStore;
   }

   public void createSubscription(String subscriptionName, boolean durable)
   {
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

   public void addRegistration(PushTopicRegistration reg) throws Exception
   {
      String destination = reg.getDestination();
      ClientSession session = sessionFactory.createSession(false, false, false);
      ClientSession.QueueQuery query = session.queueQuery(new SimpleString(destination));
      if (!query.isExists())
      {
         throw new Exception("Durable subscriber no longer exists: " + destination + " for push subscriber: " + reg.getTarget().getDelegate());
      }
      PushConsumer consumer = new PushConsumer(sessionFactory, reg.getDestination(), reg.getId(), reg);
      try
      {
         consumer.start();
      }
      catch (Exception e)
      {
         consumer.stop();
         throw new Exception("Failed starting push subscriber for " + destination + " of push subscriber: " + reg.getTarget().getDelegate(), e);
      }

      consumers.put(reg.getId(), consumer);

   }


   @POST
   public Response create(@Context UriInfo uriInfo, PushTopicRegistration registration)
   {
      //System.out.println("PushRegistration: " + registration);
      // todo put some logic here to check for duplicates
      String genId = sessionCounter.getAndIncrement() + "-topic-" + destination + "-" + startup;
      registration.setId(genId);
      registration.setDestination(genId);
      registration.setTopic(destination);
      createSubscription(genId, registration.isDurable());
      PushConsumer consumer = new PushConsumer(sessionFactory, genId, genId, registration);
      try
      {
         consumer.start();
         if (registration.isDurable() && pushStore != null)
         {
            pushStore.add(registration);
         }
      }
      catch (Exception e)
      {
         consumer.stop();
         throw new WebApplicationException(e, Response.serverError().entity("Failed to start consumer.").type("text/plain").build());
      }

      consumers.put(genId, consumer);
      UriBuilder location = uriInfo.getAbsolutePathBuilder();
      location.path(genId);
      return Response.created(location.build()).build();
   }

   @GET
   @Path("{consumer-id")
   @Produces("application/xml")
   public PushTopicRegistration getConsumer(@PathParam("consumer-id") String consumerId)
   {
      PushConsumer consumer = consumers.get(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.status(404).entity("Could not find consumer.").type("text/plain").build());
      }
      return (PushTopicRegistration) consumer.getRegistration();
   }

   @DELETE
   @Path("{consumer-id")
   public void deleteConsumer(@PathParam("consumer-id") String consumerId)
   {
      PushConsumer consumer = consumers.remove(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.status(404).entity("Could not find consumer.").type("text/plain").build());
      }
      consumer.stop();
      deleteSubscriberQueue(consumer);
   }

   public Map<String, PushConsumer> getConsumers()
   {
      return consumers;
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

   private void deleteSubscriberQueue(PushConsumer consumer)
   {
      String subscriptionName = consumer.getDestination();
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