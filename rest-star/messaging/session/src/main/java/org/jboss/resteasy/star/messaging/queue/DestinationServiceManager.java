package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.util.TimeoutTask;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class DestinationServiceManager
{
   protected ClientSessionFactory sessionFactory;
   protected ClientSessionFactory consumerSessionFactory;
   protected boolean started;
   protected String pushStoreFile;
   protected DestinationSettings defaultSettings = DestinationSettings.defaultSettings;
   protected Registry registry;
   protected TimeoutTask timeoutTask;

   public ClientSessionFactory getConsumerSessionFactory()
   {
      return consumerSessionFactory;
   }

   public void setConsumerSessionFactory(ClientSessionFactory consumerSessionFactory)
   {
      this.consumerSessionFactory = consumerSessionFactory;
   }

   public TimeoutTask getTimeoutTask()
   {
      return timeoutTask;
   }

   public void setTimeoutTask(TimeoutTask timeoutTask)
   {
      this.timeoutTask = timeoutTask;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public DestinationSettings getDefaultSettings()
   {
      return defaultSettings;
   }

   public void setDefaultSettings(DestinationSettings defaultSettings)
   {
      this.defaultSettings = defaultSettings;
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

   protected void initDefaults()
   {
      if (sessionFactory == null)
         sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));
      if (consumerSessionFactory == null) consumerSessionFactory = sessionFactory;

      if (timeoutTask == null) throw new RuntimeException("TimeoutTask is not set");
   }

   public abstract void start() throws Exception;

   public abstract void stop();
}
