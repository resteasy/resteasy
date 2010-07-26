package org.hornetq.rest.integration;

import org.jboss.resteasy.spi.Registry;
import org.hornetq.rest.MessageServiceManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RestMessagingBootstrapListener implements ServletContextListener
{
   MessageServiceManager manager;

   @Override
   public void contextInitialized(ServletContextEvent contextEvent)
   {
      ServletContext context = contextEvent.getServletContext();
      String configfile = context.getInitParameter("rest.messaging.config.file");
      Registry registry = (Registry) context.getAttribute(Registry.class.getName());
      if (registry == null)
      {
         throw new RuntimeException("You must install RESTEasy as a Bootstrap Listener and it must be listed before this class");
      }
      manager = new MessageServiceManager();

      if (configfile != null)
      {
         try
         {
            URL url = context.getResource(configfile);
            manager.setConfigurationUrl(url.toString());
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      try
      {
         manager.start();
         registry.addSingletonResource(manager.getQueueManager().getDestination());
         registry.addSingletonResource(manager.getTopicManager().getDestination());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent)
   {
      if (manager != null)
      {
         manager.stop();
      }
   }
}