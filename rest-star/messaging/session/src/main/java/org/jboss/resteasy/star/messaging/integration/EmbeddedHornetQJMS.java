package org.jboss.resteasy.star.messaging.integration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmbeddedHornetQJMS extends EmbeddedHornetQ
{
   protected JMSServerManagerImpl serverManager;
   protected ComponentRegistry registry;

   public ComponentRegistry getRegistry()
   {
      return registry;
   }

   public void setRegistry(ComponentRegistry registry)
   {
      this.registry = registry;
   }

   public void start() throws Exception
   {
      super.init();
      serverManager = new JMSServerManagerImpl(hornetQServer);
      if (registry == null)
      {
         registry = new MapComponentRegistry();
      }
      serverManager.setRegistry(registry);
      serverManager.start();
   }

   public void stop() throws Exception
   {
      serverManager.stop();
   }

}
