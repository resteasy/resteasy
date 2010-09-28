package org.jboss.resteasy.plugins.spring;

import javax.servlet.ServletContext;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringContextLoader extends ContextLoader
{
	private final static Logger logger = Logger
			.getLogger(SpringContextLoader.class);

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
      if (dispatcher == null)
         throw new RuntimeException("RESTeasy Dispatcher is null, do ou have the ResteasyBootstrap listener configured?");

      ApplicationListener listener = new ApplicationListener()
      {
         public void onApplicationEvent(ApplicationEvent event)
         {
            if (event instanceof ContextRefreshedEvent)
            {
               ContextRefreshedEvent cre = (ContextRefreshedEvent) event;
               ConfigurableListableBeanFactory autowireCapableBeanFactory = (ConfigurableListableBeanFactory) cre
                     .getApplicationContext().getAutowireCapableBeanFactory();
               new SpringBeanProcessor(dispatcher, registry, providerFactory)
                     .postProcessBeanFactory(autowireCapableBeanFactory);
            }
         }
      };
      configurableWebApplicationContext.addApplicationListener(listener);
   }
}
