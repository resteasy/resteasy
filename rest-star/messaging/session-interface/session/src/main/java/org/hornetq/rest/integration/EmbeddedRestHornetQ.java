package org.hornetq.rest.integration;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.hornetq.rest.MessageServiceManager;
import org.jboss.resteasy.test.TestPortProvider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmbeddedRestHornetQ
{
   protected TJWSEmbeddedJaxrsServer tjws = new TJWSEmbeddedJaxrsServer();
   protected EmbeddedHornetQ embeddedHornetQ;
   protected MessageServiceManager manager = new MessageServiceManager();

   public EmbeddedRestHornetQ()
   {
      int port = TestPortProvider.getPort();
      tjws.setPort(port);
      tjws.setRootResourcePath("");
      tjws.setSecurityDomain(null);
      initEmbeddedHornetQ();
   }

   protected void initEmbeddedHornetQ()
   {
      embeddedHornetQ = new EmbeddedHornetQ();
   }

   public TJWSEmbeddedJaxrsServer getTjws()
   {
      return tjws;
   }

   public void setTjws(TJWSEmbeddedJaxrsServer tjws)
   {
      this.tjws = tjws;
   }

   public EmbeddedHornetQ getEmbeddedHornetQ()
   {
      return embeddedHornetQ;
   }

   public MessageServiceManager getManager()
   {
      return manager;
   }

   public void start() throws Exception
   {
      embeddedHornetQ.start();
      tjws.start();
      manager.start();
      tjws.getDeployment().getRegistry().addSingletonResource(manager.getQueueManager().getDestination());
      tjws.getDeployment().getRegistry().addSingletonResource(manager.getTopicManager().getDestination());
   }

   public void stop() throws Exception
   {
      try
      {
         tjws.stop();
      }
      catch (Exception e)
      {
      }
      try
      {
         manager.stop();
      }
      catch (Exception e)
      {
      }
      embeddedHornetQ.stop();
   }

}
