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
 * Provides access to RESTEasy's SpringContextLoader implementation without having
 * to extend {@link org.springframework.web.context.ContextLoader}.  This is useful
 * if you have your own SpringContextLoaderListener and dont' want to 
 * return RESTEasy's {@link SpringContextLoader}.
 *
 * Usage:
 * <pre>
 * public class MyCustomSpringContextLoader extends ContextLoader 
 * {
 *    private SpringContextLoaderSupport springContextLoaderSupport = 
 *       new SpringContextLoaderSupport();
 *
 *     protected void customizeContext(
 *        ServletContext servletContext, 
 *        ConfigurableWebApplicationContext configurableWebApplicationContext)
 *    {
 *       super.customizeContext(servletContext, configurableWebApplicationContext);
 *
 *       // Your custom code 
 *
 *       this.springContextLoaderSupport.customizeContext(servletContext.configurableWebApplicationContext);
 *
 *       // Your custom code 
 *    }
 * }
 * </pre>
 */
public class SpringContextLoaderSupport 
{
   private final static Logger logger = Logger
			.getLogger(SpringContextLoader.class);

   public void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext)
   {
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
