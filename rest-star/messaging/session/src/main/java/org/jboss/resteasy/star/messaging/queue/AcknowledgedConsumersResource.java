package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;

import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcknowledgedConsumersResource extends ConsumersResource
{
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


   @Override
   protected QueueConsumer instantiate(String genId) throws HornetQException
   {
      return new AcknowledgedQueueConsumer(sessionFactory, destination, genId, ackTimeoutService, ackTimeoutSeconds);
   }
}
