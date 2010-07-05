package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.remoting.impl.invm.TransportConstants;
import org.jboss.resteasy.star.messaging.queue.DestinationSettings;
import org.jboss.resteasy.star.messaging.queue.QueueServiceManager;
import org.jboss.resteasy.star.messaging.topic.TopicServiceManager;
import org.jboss.resteasy.star.messaging.util.CustomHeaderLinkStrategy;
import org.jboss.resteasy.star.messaging.util.LinkHeaderLinkStrategy;
import org.jboss.resteasy.star.messaging.util.LinkStrategy;
import org.jboss.resteasy.star.messaging.util.TimeoutTask;

import javax.xml.bind.JAXBContext;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageServiceManager
{
   protected ExecutorService threadPool;
   protected QueueServiceManager queueManager = new QueueServiceManager();
   protected TopicServiceManager topicManager = new TopicServiceManager();
   protected TimeoutTask timeoutTask;
   protected int timeoutTaskInterval = 1;
   protected MessageServiceConfiguration configuration = new MessageServiceConfiguration();
   protected boolean configSet = false;
   protected String configurationUrl;

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

   public String getConfigurationUrl()
   {
      return configurationUrl;
   }

   public void setConfigurationUrl(String configurationUrl)
   {
      this.configurationUrl = configurationUrl;
   }

   public void setConfiguration(MessageServiceConfiguration configuration)
   {
      this.configuration = configuration;
      this.configSet = true;
   }

   public void start() throws Exception
   {
      if (configuration == null || configSet == false)
      {
         if (configurationUrl == null)
         {
            configuration = new MessageServiceConfiguration();
         }
         else
         {
            URL url = getClass().getClassLoader().getResource(configurationUrl);

            if (url == null)
            {
               // The URL is outside of the classloader. Trying a pure url now
               url = new URL(configurationUrl);
            }
            JAXBContext jaxb = JAXBContext.newInstance(MessageServiceConfiguration.class);
            Reader reader = new InputStreamReader(url.openStream());
            configuration = (MessageServiceConfiguration) jaxb.createUnmarshaller().unmarshal(reader);


         }
      }
      if (threadPool == null) threadPool = Executors.newCachedThreadPool();
      timeoutTaskInterval = configuration.getTimeoutTaskInterval();
      timeoutTask = new TimeoutTask(timeoutTaskInterval);
      threadPool.execute(timeoutTask);

      DestinationSettings defaultSettings = new DestinationSettings();
      defaultSettings.setConsumerSessionTimeoutSeconds(configuration.getConsumerSessionTimeoutSeconds());
      defaultSettings.setDuplicatesAllowed(configuration.isDupsOk());
      defaultSettings.setDurableSend(configuration.isDefaultDurableSend());

      HashMap<String, Object> transportConfig = new HashMap<String, Object>();
      transportConfig.put(TransportConstants.SERVER_ID_PROP_NAME, configuration.getInVmId());
      ClientSessionFactory consumerSessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));
      if (configuration.getConsumerWindowSize() != -1)
      {
         consumerSessionFactory.setConsumerWindowSize(configuration.getConsumerWindowSize());
      }

      ClientSessionFactory sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));

      LinkStrategy linkStrategy = new LinkHeaderLinkStrategy();
      if (configuration.isUseLinkHeaders())
      {
         linkStrategy = new LinkHeaderLinkStrategy();
      }
      else
      {
         linkStrategy = new CustomHeaderLinkStrategy();
      }

      queueManager.setSessionFactory(sessionFactory);
      queueManager.setTimeoutTask(timeoutTask);
      queueManager.setConsumerSessionFactory(consumerSessionFactory);
      queueManager.setDefaultSettings(defaultSettings);
      queueManager.setPushStoreFile(configuration.getQueuePushStoreFile());
      queueManager.setProducerPoolSize(configuration.getProducerSessionPoolSize());
      queueManager.setLinkStrategy(linkStrategy);

      topicManager.setSessionFactory(sessionFactory);
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
