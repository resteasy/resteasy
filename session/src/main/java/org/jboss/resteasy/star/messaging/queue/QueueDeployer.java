package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.push.PushConsumerResource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueDeployer
{
   protected Registry registry;
   protected List<QueueDeployment> queues = new ArrayList<QueueDeployment>();
   protected QueueDestinationsResource destination;
   protected ExecutorService ackTimeoutExecutorService;
   protected ClientSessionFactory sessionFactory;
   protected boolean started;

   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public QueueDestinationsResource getDestination()
   {
      return destination;
   }

   public void setDestination(QueueDestinationsResource destination)
   {
      this.destination = destination;
   }

   public List<QueueDeployment> getQueues()
   {
      return queues;
   }

   public void setQueues(List<QueueDeployment> queues)
   {
      this.queues = queues;
   }

   public ExecutorService getAckTimeoutExecutorService()
   {
      return ackTimeoutExecutorService;
   }

   public void setAckTimeoutExecutorService(ExecutorService ackTimeoutExecutorService)
   {
      this.ackTimeoutExecutorService = ackTimeoutExecutorService;
   }

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public void start() throws Exception
   {

      if (ackTimeoutExecutorService == null) ackTimeoutExecutorService = Executors.newCachedThreadPool();
      if (sessionFactory == null)
         sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));


      destination = new QueueDestinationsResource();

      started = true;

      for (QueueDeployment queueDeployment : queues)
      {
         deploy(queueDeployment);
      }
      registry.addSingletonResource(destination);
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
      boolean defaultDurable = queueDeployment.isDurableSend();
      if (query.isExists())
      {
         defaultDurable = query.isDurable();
      }
      else
      {
         session.createQueue(queueName, queueName, queueDeployment.isDurableSend());
      }
      session.close();

      QueueResource queueResource = new QueueResource();
      queueResource.setDestination(queueName);

      ConsumersResource consumers = null;
      if (queueDeployment.isAutoAcknowledge())
      {
         consumers = new ConsumersResource();
      }
      else
      {
         AcknowledgedConsumersResource acked = new AcknowledgedConsumersResource();
         acked.setAckTimeoutSeconds(queueDeployment.getAckTimeoutSeconds());
         acked.setAckTimeoutService(ackTimeoutExecutorService);
         consumers = acked;
      }
      consumers.setDestination(queueName);
      consumers.setSessionFactory(sessionFactory);
      queueResource.setConsumers(consumers);

      PushConsumerResource push = new PushConsumerResource();
      push.setDestination(queueName);
      push.setSessionFactory(sessionFactory);
      queueResource.setPushConsumers(push);

      PostMessage sender = null;
      if (queueDeployment.isDuplicatesAllowed())
      {
         sender = new PostMessageDupsOk();
      }
      else
      {
         sender = new PostMessageNoDups();
      }
      sender.setDefaultDurable(defaultDurable);
      sender.setDestination(queueName);
      sender.setSessionFactory(sessionFactory);
      queueResource.setSender(sender);

      destination.getQueues().put(queueName, queueResource);
      queueResource.start();
   }

   public void stop() throws Exception
   {
      for (QueueResource queue : destination.getQueues().values())
      {
         queue.stop();
      }
      try
      {
         sessionFactory.close();
      }
      catch (Exception e)
      {
      }
   }
}
