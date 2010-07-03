package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.jboss.resteasy.star.messaging.queue.push.FilePushStore;
import org.jboss.resteasy.star.messaging.queue.push.PushStore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueServiceManager extends DestinationServiceManager
{
   protected PushStore pushStore;
   protected List<QueueDeployment> queues = new ArrayList<QueueDeployment>();
   protected QueueDestinationsResource destination;

   public List<QueueDeployment> getQueues()
   {
      return queues;
   }

   public void setQueues(List<QueueDeployment> queues)
   {
      this.queues = queues;
   }

   public PushStore getPushStore()
   {
      return pushStore;
   }

   public void setPushStore(PushStore pushStore)
   {
      this.pushStore = pushStore;
   }

   public QueueDestinationsResource getDestination()
   {
      return destination;
   }

   public void setDestination(QueueDestinationsResource destination)
   {
      this.destination = destination;
   }

   @Override
   public void start() throws Exception
   {
      initDefaults();

      destination = new QueueDestinationsResource(this);

      started = true;

      if (pushStoreFile != null && pushStore == null)
      {
         pushStore = new FilePushStore(pushStoreFile);
      }

      for (QueueDeployment queueDeployment : queues)
      {
         deploy(queueDeployment);
      }
   }

   public void deploy(QueueDeployment queueDeployment)
           throws Exception
   {
      if (!started)
      {
         throw new Exception("You must start() this class instance before deploying");
      }
      String queueName = queueDeployment.getName();
      ClientSession session = sessionFactory.createSession(false, false, false);
      ClientSession.QueueQuery query = session.queueQuery(new SimpleString(queueName));
      if (!query.isExists())
      {
         session.createQueue(queueName, queueName, queueDeployment.isDurableSend());
      }
      session.close();

      destination.createQueueResource(queueName, queueDeployment.isDurableSend(), queueDeployment.getConsumerSessionTimeoutSeconds(), queueDeployment.isDuplicatesAllowed());

   }

   @Override
   public void stop()
   {
      for (QueueResource queue : destination.getQueues().values())
      {
         queue.stop();
      }
      try
      {
         timeoutTask.stop();
         sessionFactory.close();
      }
      catch (Exception e)
      {
      }
   }
}
