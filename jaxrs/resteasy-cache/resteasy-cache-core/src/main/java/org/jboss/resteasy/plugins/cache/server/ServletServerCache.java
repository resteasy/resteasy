package org.jboss.resteasy.plugins.cache.server;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletServerCache implements ServletContextListener
{
   protected ResteasyProviderFactory providerFactory;
   protected JBossCache cache = new JBossCache();

   public void contextInitialized(ServletContextEvent servletContextEvent)
   {
      ServletContext servletContext = servletContextEvent.getServletContext();
      providerFactory = (ResteasyProviderFactory) servletContext.getAttribute(ResteasyProviderFactory.class.getName());
      if (providerFactory == null)
      {
         throw new RuntimeException("Resteasy is not intialized, could not find ResteasyProviderFactory attribute");
      }

      String maxSize = servletContext.getInitParameter("resteasy.server.cache.maxsize");
      if (maxSize != null)
      {
         cache.setMaxSize(Integer.parseInt(maxSize));
      }

      String wakeupInterval = servletContext.getInitParameter("resteasy.server.cache.eviction.wakeup.interval");
      if (wakeupInterval != null)
      {
         cache.setWakeupInterval(Long.parseLong(wakeupInterval));
      }
      cache.setProviderFactory(providerFactory);

      cache.start();
   }

   public void contextDestroyed(ServletContextEvent servletContextEvent)
   {
      cache.stop();
   }
}
