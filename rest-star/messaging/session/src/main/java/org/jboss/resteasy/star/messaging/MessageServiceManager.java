package org.jboss.resteasy.star.messaging;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.QueueServiceManager;
import org.jboss.resteasy.star.messaging.topic.TopicServiceManager;
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

   public int getTimeoutTaskInterval()
   {
      return timeoutTaskInterval;
   }

   public void setTimeoutTaskInterval(int timeoutTaskInterval)
   {
      this.timeoutTaskInterval = timeoutTaskInterval;
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
      timeoutTask = new TimeoutTask(timeoutTaskInterval);
      threadPool.execute(timeoutTask);

      queueManager.setTimeoutTask(timeoutTask);
      queueManager.setThreadPool(threadPool);
      queueManager.setRegistry(registry);

      topicManager.setThreadPool(threadPool);
      topicManager.setRegistry(registry);
      topicManager.setTimeoutTask(timeoutTask);

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
