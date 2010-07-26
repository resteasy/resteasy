package org.hornetq.rest;

import org.jboss.resteasy.spi.Registry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicServletContextListener implements ServletContextListener
{
   protected SimpleDeployment deployment = new SimpleDeployment();

   public void contextInitialized(ServletContextEvent event)
   {
      final Registry registry = (Registry) event.getServletContext().getAttribute(Registry.class.getName());
      if (registry == null)
         throw new RuntimeException("RESTeasy Registry is null, do ou have the ResteasyBootstrap listener configured?");

      deployment.setRegistry(registry);
      String topics = event.getServletContext().getInitParameter("resteasy.star.messaging.topics");
      if (topics == null)
      {
         throw new RuntimeException("There were no topics declared within resteasy.star.messaging.topics");

      }
      String[] split = topics.split(",");
      for (String topic : split)
      {
         deployment.getTopics().add(new TopicDeployment(topic, true));
      }
      try
      {
         deployment.start();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void contextDestroyed(ServletContextEvent servletContextEvent)
   {
      try
      {
         deployment.stop();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
