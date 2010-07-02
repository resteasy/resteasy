package org.jboss.resteasy.star.messaging.test;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.jboss.resteasy.star.messaging.MessageServiceManager;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BaseMessageTest extends BaseResourceTest
{
   public static HornetQServer server;
   public static MessageServiceManager manager;

   @BeforeClass
   public static void setupHornetQServerAndManager() throws Exception
   {
      setupHornetQServer();

      manager = new MessageServiceManager();
      manager.setRegistry(deployment.getRegistry());
      manager.start();
   }

   public static void setupHornetQServer()
           throws Exception
   {
      Configuration configuration = new ConfigurationImpl();
      configuration.setPersistenceEnabled(false);
      configuration.setSecurityEnabled(false);
      configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

      server = HornetQServers.newHornetQServer(configuration);
      server.start();
   }

   @AfterClass
   public static void shutdownHornetqServerAndManager() throws Exception
   {
      manager.stop();
      manager = null;
      shutdownHornetqServer();
   }

   public static void shutdownHornetqServer() throws Exception
   {
      server.stop();
      server = null;
   }

}
