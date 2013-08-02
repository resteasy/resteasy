package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Use this subclass of SpringBeanProcessor if you are manually applying the SpringBeanProcessor for Resteasy.  This assumes that the
 * Registry and ResteasyProviderFactory objects are registered as attributes in the ServletContext
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessorServletAware extends SpringBeanProcessor implements ServletContextAware
{
   private ServletContext servletContext;

   public void setServletContext(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   @Override
   public Registry getRegistry()
   {
      if (registry != null) return registry;
      registry = (Registry) servletContext.getAttribute(Registry.class.getName());
      return registry;
   }

   @Override
   public ResteasyProviderFactory getProviderFactory()
   {
      if (providerFactory != null) return providerFactory;
      providerFactory = (ResteasyProviderFactory) servletContext.getAttribute(ResteasyProviderFactory.class.getName());
      return providerFactory;
   }

   @Override
   public Dispatcher getDispatcher()
   {
      if (dispatcher != null) return dispatcher;
      dispatcher = (Dispatcher) servletContext.getAttribute(Dispatcher.class.getName());
      return dispatcher;
   }
}
