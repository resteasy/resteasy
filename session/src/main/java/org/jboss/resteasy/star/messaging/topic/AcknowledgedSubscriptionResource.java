package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.queue.AcknowledgedQueueConsumer;
import org.jboss.resteasy.star.messaging.queue.ConsumerFactory;
import org.jboss.resteasy.star.messaging.queue.QueueConsumer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcknowledgedSubscriptionResource extends AcknowledgedQueueConsumer implements Subscription
{
   private boolean durable;

   public AcknowledgedSubscriptionResource(ClientSessionFactory factory, String destination, String id, ExecutorService ackTimeoutService, long ackTimeoutSeconds)
           throws HornetQException
   {
      super(factory, destination, id, ackTimeoutService, ackTimeoutSeconds);
   }

   public boolean isDurable()
   {
      return durable;
   }

   public void setDurable(boolean durable)
   {
      this.durable = durable;
   }

   @Override
   protected void setAcknowledgeLinks(UriInfo uriInfo, String basePath, Response.ResponseBuilder builder)
   {
      setConsumeNextLink(builder, uriInfo, basePath);
      SubscriptionResource.setSubscriptionLink(builder, uriInfo, basePath);
   }

   @Override
   protected void setMessageResponseLinks(UriInfo info, String basePath, Response.ResponseBuilder builder)
   {
      setConsumeNextLink(builder, info, basePath);
      SubscriptionResource.setSubscriptionLink(builder, info, basePath);
      setAcknowledgementLink(builder, info, basePath);
   }

   public static ConsumerFactory getFactory(final ExecutorService ackTimeoutExecutorService, final long ackTimeoutSeconds)
   {
      return new ConsumerFactory()
      {
         @Override
         public QueueConsumer createConsumer(String id, ClientSessionFactory factory, String destination) throws HornetQException
         {
            return new AcknowledgedSubscriptionResource(factory, destination, id, ackTimeoutExecutorService, ackTimeoutSeconds);
         }
      };
   }
}
