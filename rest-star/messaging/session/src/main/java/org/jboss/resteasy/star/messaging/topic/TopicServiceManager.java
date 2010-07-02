package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.DestinationSettings;
import org.jboss.resteasy.star.messaging.util.TimeoutTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicServiceManager
{
   protected Registry registry;
   protected List<TopicDeployment> topics = new ArrayList<TopicDeployment>();
   protected TopicDestinationsResource destination;
   protected ExecutorService threadPool;
   protected ClientSessionFactory sessionFactory;
   protected boolean started;
   protected String pushStoreFile;
   protected TopicPushStore pushStore;
   protected DestinationSettings defaultSettings = DestinationSettings.defaultSettings;
   protected TimeoutTask timeoutTask;
   protected int timeoutTaskInterval = 1;

   public TimeoutTask getTimeoutTask()
   {
      return timeoutTask;
   }

   public void setTimeoutTask(TimeoutTask timeoutTask)
   {
      this.timeoutTask = timeoutTask;
   }

   public int getTimeoutTaskInterval()
   {
      return timeoutTaskInterval;
   }

   public void setTimeoutTaskInterval(int timeoutTaskInterval)
   {
      this.timeoutTaskInterval = timeoutTaskInterval;
   }

   public DestinationSettings getDefaultSettings()
   {
      return defaultSettings;
   }

   public void setDefaultSettings(DestinationSettings defaultSettings)
   {
      this.defaultSettings = defaultSettings;
   }

   public String getPushStoreFile()
   {
      return pushStoreFile;
   }

   public void setPushStoreFile(String pushStoreFile)
   {
      this.pushStoreFile = pushStoreFile;
   }

   public TopicPushStore getPushStore()
   {
      return pushStore;
   }

   public void setPushStore(TopicPushStore pushStore)
   {
      this.pushStore = pushStore;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public List<TopicDeployment> getTopics()
   {
      return topics;
   }

   public void setTopics(List<TopicDeployment> topics)
   {
      this.topics = topics;
   }

   public ExecutorService getThreadPool()
   {
      return threadPool;
   }

   public void setThreadPool(ExecutorService threadPool)
   {
      this.threadPool = threadPool;
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

      if (sessionFactory == null)
         sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));
      if (timeoutTask == null)
      {
         if (threadPool == null) threadPool = Executors.newCachedThreadPool();
         timeoutTask = new TimeoutTask(timeoutTaskInterval);
         threadPool.execute(timeoutTask);
      }


      started = true;

      if (pushStoreFile != null && pushStore == null)
      {
         pushStore = new FileTopicPushStore(pushStoreFile);
      }

      if (destination == null)
      {
         destination = new TopicDestinationsResource(this);
      }

      for (TopicDeployment topic : topics)
      {
         deploy(topic);
      }
      registry.addSingletonResource(destination);
   }

   public void deploy(TopicDeployment topicDeployment)
           throws Exception
   {
      if (!started)
      {
         throw new Exception("You must start() this class instance before deploying");
      }
      String queueName = topicDeployment.getName();
      ClientSession session = sessionFactory.createSession(false, false, false);
      ClientSession.QueueQuery query = session.queueQuery(new SimpleString(queueName));
      boolean defaultDurable = topicDeployment.isDurableSend();
      if (query.isExists())
      {
         defaultDurable = query.isDurable();
      }
      else
      {
         session.createQueue(queueName, queueName, topicDeployment.isDurableSend());
      }
      session.close();

      destination.createTopicResource(queueName, defaultDurable, topicDeployment.getConsumerSessionTimeoutSeconds(), topicDeployment.isDuplicatesAllowed());
   }

   public void stop()
   {
      for (TopicResource topic : destination.getTopics().values())
      {
         topic.stop();
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