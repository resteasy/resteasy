package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSessionFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConsumersResource
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
      }
   }

   public QueueConsumer createConsumer()
           throws HornetQException
   {
      String genId = sessionCounter.getAndIncrement() + "-queue-" + destination + "-" + startup;
      QueueConsumer consumer = new QueueConsumer(sessionFactory, destination, genId);
      queueConsumers.put(genId, consumer);
      return consumer;
   }

   public QueueConsumer createAcknowledgedConsumer()
           throws HornetQException
   {
      String genId = sessionCounter.getAndIncrement() + "-queue-" + destination + "-" + startup;
      QueueConsumer consumer = new AcknowledgedQueueConsumer(sessionFactory, destination, genId, ackTimeoutService, ackTimeoutSeconds);
      queueConsumers.put(genId, consumer);
      return consumer;
   }

   @Path("{consumer-id}")
   public QueueConsumer getConsumer(
           @PathParam("consumer-id") String consumerId)
   {
      QueueConsumer consumer = queueConsumers.get(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.serverError()
                 .entity("Failed to match a consumer to URL" + consumerId)
                 .type("text/plain").build());
      }
      return consumer;
   }


   @Path("{consumer-id}")
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
