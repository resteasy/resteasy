package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.util.TimeoutTask;

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
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConsumersResource implements TimeoutTask.Callback
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

   public int getConsumerTimeoutSeconds()
   {
      return consumerTimeoutSeconds;
   }

   public void setConsumerTimeoutSeconds(int consumerTimeoutSeconds)
   {
      this.consumerTimeoutSeconds = consumerTimeoutSeconds;
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
      }
   }

   @POST
   public Response createSubscription(@FormParam("autoAck") @DefaultValue("true") boolean autoAck,
                                      @Context UriInfo uriInfo)
   {
      try
      {
         QueueConsumer consumer = null;
         if (autoAck)
         {
            consumer = createConsumer();
         }
         else
         {
            consumer = createAcknowledgedConsumer();
         }

         UriBuilder location = uriInfo.getAbsolutePathBuilder();
         if (autoAck) location.path("auto-ack");
         else location.path("acknowledged");
         location.path(consumer.getId());
         Response.ResponseBuilder builder = Response.created(location.build());
         if (autoAck)
         {
            QueueConsumer.setConsumeNextLink(serviceManager.getLinkStrategy(), builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/auto-ack/" + consumer.getId(), "-1");
         }
         else
         {
            AcknowledgedQueueConsumer.setAcknowledgeNextLink(serviceManager.getLinkStrategy(), builder, uriInfo, uriInfo.getMatchedURIs().get(1) + "/acknowledged/" + consumer.getId(), "-1");

         }
         return builder.build();

      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
      }
   }

   public QueueConsumer createConsumer()
           throws HornetQException
   {
      String genId = sessionCounter.getAndIncrement() + "-queue-" + destination + "-" + startup;
      QueueConsumer consumer = new QueueConsumer(sessionFactory, destination, genId, serviceManager);
      synchronized (timeoutLock)
      {
         queueConsumers.put(genId, consumer);
         serviceManager.getTimeoutTask().add(this, consumer.getId());
      }
      return consumer;
   }

   public QueueConsumer createAcknowledgedConsumer()
           throws HornetQException
   {
      String genId = sessionCounter.getAndIncrement() + "-queue-" + destination + "-" + startup;
      QueueConsumer consumer = new AcknowledgedQueueConsumer(sessionFactory, destination, genId, serviceManager);
      synchronized (timeoutLock)
      {
         queueConsumers.put(genId, consumer);
         serviceManager.getTimeoutTask().add(this, consumer.getId());
      }
      return consumer;
   }

   @Path("auto-ack/{consumer-id}")
   public QueueConsumer getConsumer(
           @PathParam("consumer-id") String consumerId) throws Exception
   {
      QueueConsumer consumer = queueConsumers.get(consumerId);
      if (consumer == null)
      {
         QueueConsumer tmp = new QueueConsumer(sessionFactory, destination, consumerId, serviceManager);
         consumer = putConsumer(consumerId, tmp);
      }
      return consumer;
   }

   @Path("acknowledged/{consumer-id}")
   public QueueConsumer getAcknowledgedConsumer(
           @PathParam("consumer-id") String consumerId) throws Exception
   {
      QueueConsumer consumer = queueConsumers.get(consumerId);
      if (consumer == null)
      {
         QueueConsumer tmp = new AcknowledgedQueueConsumer(sessionFactory, destination, consumerId, serviceManager);
         ;
         consumer = putConsumer(consumerId, tmp);
      }
      return consumer;
   }

   private QueueConsumer putConsumer(String consumerId, QueueConsumer tmp)
   {
      synchronized (timeoutLock)
      {
         QueueConsumer consumer;
         consumer = queueConsumers.putIfAbsent(consumerId, tmp);
         if (consumer != null)
         {
            tmp.shutdown();
         }
         else
         {
            consumer = tmp;
            serviceManager.getTimeoutTask().add(this, consumer.getId());
         }
         return consumer;
      }
   }


   @Path("acknowledged/{consumer-id}")
   @DELETE
   public void closeAcknowledgedSession(
           @PathParam("consumer-id") String consumerId)
   {
      closeSession(consumerId);
   }

   @Path("auto-ack/{consumer-id}")
   @DELETE
   public void closeSession(
           @PathParam("consumer-id") String consumerId)
   {
      QueueConsumer consumer = queueConsumers.remove(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                 .entity("Failed to match a consumer to URL" + consumerId)
                 .type("text/plain").build());
      }
      consumer.shutdown();
   }
}
