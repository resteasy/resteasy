package org.hornetq.rest.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.rest.queue.AcknowledgedQueueConsumer;
import org.hornetq.rest.queue.DestinationServiceManager;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcknowledgedSubscriptionResource extends AcknowledgedQueueConsumer implements Subscription
{
   private boolean durable;

   public AcknowledgedSubscriptionResource(ClientSessionFactory factory, String destination, String id, DestinationServiceManager serviceManager, String selector)
           throws HornetQException
   {
      super(factory, destination, id, serviceManager, selector);
   }

   public boolean isDurable()
   {
      return durable;
   }

   public void setDurable(boolean durable)
   {
      this.durable = durable;
   }

}
