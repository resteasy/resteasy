package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.DestinationSettings;
import org.jboss.resteasy.star.messaging.queue.QueueServiceManager;
import org.jboss.resteasy.star.messaging.topic.TopicServiceManager;
import org.jboss.resteasy.star.messaging.util.CustomHeaderLinkStrategy;
import org.jboss.resteasy.star.messaging.util.LinkHeaderLinkStrategy;
import org.jboss.resteasy.star.messaging.util.LinkStrategy;
import org.jboss.resteasy.star.messaging.util.TimeoutTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageServiceManager
{
   protected Registry registry;
   protected ExecutorService threadPool;
   protected QueueServiceManager queueManager = new QueueServiceManager();
   protected TopicServiceManager topicManager = new TopicServiceManager();
   protected TimeoutTask timeoutTask;
   protected int timeoutTaskInterval = 1;
   protected MessageServiceConfiguration configuration = new MessageServiceConfiguration();

   public int getTimeoutTaskInterval()
   {
      return timeoutTaskInterval;
   }

   public void setTimeoutTaskInterval(int timeoutTaskInterval)
   {
      this.timeoutTaskInterval = timeoutTaskInterval;
      if (timeoutTask != null)
      {
         timeoutTask.setInterval(timeoutTaskInterval);
      }
   }

   public ExecutorService getThreadPool()
   {
      return threadPool;
   }

   public void setThreadPool(ExecutorService threadPool)
   {
      this.threadPool = threadPool;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public QueueServiceManager getQueueManager()
   {
      return queueManager;
   }

   public TopicServiceManager getTopicManager()
   {
      return topicManager;
   }

   public MessageServiceConfiguration getConfiguration()
   {
      return configuration;
   }

   public void setConfiguration(MessageServiceConfiguration configuration)
   {
      this.configuration = configuration;
   }

   public void start() throws Exception
   {
      if (threadPool == null) threadPool = Executors.newCachedThreadPool();
      if (configuration == null) configuration = new MessageServiceConfiguration();
      timeoutTaskInterval = configuration.getTimeoutTaskInterval();
      timeoutTask = new TimeoutTask(timeoutTaskInterval);
      threadPool.execute(timeoutTask);

      DestinationSettings defaultSettings = new DestinationSettings();
      defaultSettings.setConsumerSessionTimeoutSeconds(configuration.getConsumerSessionTimeoutSeconds());
      defaultSettings.setDuplicatesAllowed(configuration.isDupsOk());
      defaultSettings.setDurableSend(configuration.isDefaultDurableSend());

      ClientSessionFactory consumerSessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));
      if (configuration.getConsumerWindowSize() != -1)
      {
         consumerSessionFactory.setConsumerWindowSize(configuration.getConsumerWindowSize());
      }

      LinkStrategy linkStrategy = new LinkHeaderLinkStrategy();
      if (configuration.isUseLinkHeaders())
      {
         linkStrategy = new LinkHeaderLinkStrategy();
      }
      else
      {
         linkStrategy = new CustomHeaderLinkStrategy();
      }

      queueManager.setTimeoutTask(timeoutTask);
      queueManager.setRegistry(registry);
      queueManager.setConsumerSessionFactory(consumerSessionFactory);
      queueManager.setDefaultSettings(defaultSettings);
      queueManager.setPushStoreFile(configuration.getQueuePushStoreFile());
      queueManager.setProducerPoolSize(configuration.getProducerSessionPoolSize());
      queueManager.setLinkStrategy(linkStrategy);

      topicManager.setRegistry(registry);
      topicManager.setTimeoutTask(timeoutTask);
      topicManager.setConsumerSessionFactory(consumerSessionFactory);
      topicManager.setDefaultSettings(defaultSettings);
      topicManager.setPushStoreFile(configuration.getTopicPushStoreFile());
      topicManager.setProducerPoolSize(configuration.getProducerSessionPoolSize());
      topicManager.setLinkStrategy(linkStrategy);

      queueManager.start();
      topicManager.start();
   }

   public void stop()
   {
      queueManager.stop();
      queueManager = null;
      topicManager.stop();
      topicManager = null;
   }

}
