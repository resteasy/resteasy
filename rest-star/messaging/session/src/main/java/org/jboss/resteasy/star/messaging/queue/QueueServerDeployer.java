package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;

/**
 * Assembles all the pieces for a simple server
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueServerDeployer extends QueueDeployer
{
   protected HornetQServer server;
   protected Configuration configuration;

   public Configuration getConfiguration()
   {
      return configuration;
   }

   public void setConfiguration(Configuration configuration)
   {
      this.configuration = configuration;
   }

   /**
    * Start an instance of the server
    *
    * @throws Exception
    */
   public void start()
           throws Exception
   {
      if (configuration == null)
      {
         configuration = new ConfigurationImpl();
         configuration.setPersistenceEnabled(false);
         configuration.setSecurityEnabled(false);
         configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));
      }


      // Step 2. Create and start the server
      server = HornetQServers.newHornetQServer(configuration);
      server.start();
      super.start();
   }

   public void stop() throws Exception
   {
      try
      {
         super.stop();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      try
      {
         if (server != null) server.stop();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

}
