package org.jboss.resteasy.star.messaging.integration;

import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.impl.HornetQServerImpl;
import org.hornetq.spi.core.security.HornetQSecurityManager;
import org.hornetq.spi.core.security.HornetQSecurityManagerImpl;

import javax.management.MBeanServer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmbeddedHornetQ
{
   protected HornetQSecurityManager securityManager;
   protected FileConfiguration configuration = new FileConfiguration();
   protected HornetQServer hornetQServer;
   protected MBeanServer mbeanServer;

   public void setConfigurationFile(String filename)
   {
      configuration.setConfigurationUrl(filename);
   }

   public void setSecurityManager(HornetQSecurityManager securityManager)
   {
      this.securityManager = securityManager;
   }

   public HornetQServer getHornetQServer()
   {
      return hornetQServer;
   }

   public void start() throws Exception
   {
      initStart();
      hornetQServer.start();

   }

   protected void initStart() throws Exception
   {
      configuration.start();
      if (securityManager == null)
      {
         securityManager = new HornetQSecurityManagerImpl();
      }
      if (mbeanServer == null)
      {
         hornetQServer = new HornetQServerImpl(configuration, securityManager);
      }
      else
      {
         hornetQServer = new HornetQServerImpl(configuration, mbeanServer, securityManager);
      }
   }

   public void stop() throws Exception
   {
      hornetQServer.stop();
   }
}
