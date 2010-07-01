package org.jboss.resteasy.star.messaging;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.QueueServiceManager;
import org.jboss.resteasy.star.messaging.topic.TopicServiceManager;

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

   public void setQueueManager(QueueServiceManager queueManager)
   {
      this.queueManager = queueManager;
   }

   public TopicServiceManager getTopicManager()
   {
      return topicManager;
   }

   public void setTopicManager(TopicServiceManager topicManager)
   {
      this.topicManager = topicManager;
   }

   public void start() throws Exception
   {
      if (threadPool == null) threadPool = Executors.newCachedThreadPool();

      queueManager.setThreadPool(threadPool);
      topicManager.setThreadPool(threadPool);
      queueManager.setRegistry(registry);
      topicManager.setRegistry(registry);

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
