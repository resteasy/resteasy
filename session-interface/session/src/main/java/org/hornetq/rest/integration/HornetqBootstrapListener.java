package org.hornetq.rest.integration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HornetqBootstrapListener implements ServletContextListener
{
   private EmbeddedHornetQJMS jms;

   @Override
   public void contextInitialized(ServletContextEvent contextEvent)
   {
      ServletContext context = contextEvent.getServletContext();
      jms = new EmbeddedHornetQJMS();
      jms.setRegistry(new ServletContextComponentRegistry(context));
      try
      {
         jms.start();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent)
   {
      try
      {
         if (jms != null) jms.stop();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
