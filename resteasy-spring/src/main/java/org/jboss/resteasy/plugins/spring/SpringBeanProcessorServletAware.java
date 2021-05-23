package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.web.context.ServletContextAware;

import jakarta.servlet.ServletContext;

/**
 * Use this subclass of SpringBeanProcessor if you are manually applying the SpringBeanProcessor for Resteasy.  This assumes that the
 * Registry and ResteasyProviderFactory objects are registered as attributes in the ServletContext
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessorServletAware extends SpringBeanProcessor implements ServletContextAware
{
   protected ServletContext servletContext;

   public void setServletContext(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   @Override
   public Registry getRegistry()
   {
      if (registry != null) return registry;
      ResteasyDeployment deployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
      if (deployment != null)
      {
         registry = deployment.getRegistry();
      }
      return registry;
   }

   @Override
   public ResteasyProviderFactory getProviderFactory()
   {
      if (providerFactory != null) return providerFactory;
      ResteasyDeployment deployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
      if (deployment != null)
      {
         providerFactory = deployment.getProviderFactory();
      }
      return providerFactory;
   }

   @Override
   public Dispatcher getDispatcher()
   {
      if (dispatcher != null) return dispatcher;
      ResteasyDeployment deployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
      if (deployment != null)
      {
         dispatcher = deployment.getDispatcher();
      }
      return dispatcher;
   }
}
