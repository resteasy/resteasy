package org.resteasy.plugins.server.servlet;

import org.resteasy.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * This is a ServletContextListener that creates the registry for resteasy and stuffs it as a servlet context attribute
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyBootstrap implements ServletContextListener
{
   private ResteasyProviderFactory factory = new ResteasyProviderFactory();
   private Registry registry = new Registry(factory);

   public void contextInitialized(ServletContextEvent event)
   {
      event.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), factory);
      event.getServletContext().setAttribute(Registry.class.getName(), registry);

      String providers = event.getServletContext().getInitParameter("resteasy.providers");
      if (providers != null) setProviders(providers);
      String scanProviders = event.getServletContext().getInitParameter("resteasy.scan.providers");
      if (scanProviders != null)
      {
      }
      String scanAll = event.getServletContext().getInitParameter("resteasy.scan");
      String scanResources = event.getServletContext().getInitParameter("resteasy.scan.resources");
      String jndiResources = event.getServletContext().getInitParameter("resteasy.jndi.resources");
   }

   protected void setProviders(String providers)
   {
      String[] p = providers.split(",");
      for (String provider : p)
      {
         provider = provider.trim();
         Object obj = null;
         try
         {
            Class prov = Thread.currentThread().getContextClassLoader().loadClass(provider);
            obj = prov.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         if (obj instanceof MessageBodyReader) factory.addMessageBodyReader((MessageBodyReader) obj);
         if (obj instanceof MessageBodyWriter) factory.addMessageBodyWriter((MessageBodyWriter) obj);
      }
   }

   public void contextDestroyed(ServletContextEvent event)
   {
   }
}
