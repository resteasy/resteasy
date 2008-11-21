package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringContextLoader extends ContextLoader
{
   protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext)
   {
      super.customizeContext(servletContext, configurableWebApplicationContext);

      final ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) servletContext.getAttribute(ResteasyProviderFactory.class.getName());
      if (providerFactory == null)
         throw new RuntimeException("RESTeasy Provider Factory is null, do you have the ResteasyBootstrap listener configured?");


      final Registry registry = (Registry) servletContext.getAttribute(Registry.class.getName());
      if (registry == null)
         throw new RuntimeException("RESTeasy Registry is null, do ou have the ResteasyBootstrap listener configured?");

      final Dispatcher dispatcher = (Dispatcher) servletContext.getAttribute(Dispatcher.class.getName());
      if (registry == null)
         throw new RuntimeException("RESTeasy Dispatcher is null, do ou have the ResteasyBootstrap listener configured?");

      configurableWebApplicationContext.addBeanFactoryPostProcessor(new SpringBeanProcessor(dispatcher, registry, providerFactory));

   }
}
