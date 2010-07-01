package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.push.FilePushStore;
import org.jboss.resteasy.star.messaging.queue.push.PushStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueServiceManager
{
   protected Registry registry;
   protected List<QueueDeployment> queues = new ArrayList<QueueDeployment>();
   protected QueueDestinationsResource destination;
   protected ExecutorService ackTimeoutExecutorService;
   protected ClientSessionFactory sessionFactory;
   protected boolean started;
   protected String pushStoreFile;
   protected PushStore pushStore;
   protected DestinationSettings defaultSettings = DestinationSettings.defaultSettings;

   public DestinationSettings getDefaultSettings()
   {
      return defaultSettings;
   }

   public void setDefaultSettings(DestinationSettings defaultSettings)
   {
      this.defaultSettings = defaultSettings;
   }

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

   public String getPushStoreFile()
   {
      return pushStoreFile;
   }

   public void setPushStoreFile(String pushStoreFile)
   {
      this.pushStoreFile = pushStoreFile;
   }

   public PushStore getPushStore()
   {
      return pushStore;
   }

   public void setPushStore(PushStore pushStore)
   {
      this.pushStore = pushStore;
   }

   public void start() throws Exception
   {

      if (ackTimeoutExecutorService == null) ackTimeoutExecutorService = Executors.newCachedThreadPool();
      if (sessionFactory == null)
         sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));


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
      if (!query.isExists())
      {
         session.createQueue(queueName, queueName, queueDeployment.isDurableSend());
      }
      session.close();

      destination.createQueueResource(queueName, queueDeployment.isDurableSend(), queueDeployment.getAckTimeoutSeconds(), queueDeployment.isDuplicatesAllowed());

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
