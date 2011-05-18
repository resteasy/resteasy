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

   private SpringContextLoaderSupport springContextLoaderSupport = new SpringContextLoaderSupport();

   protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext)
   {
      super.customizeContext(servletContext, configurableWebApplicationContext);
      this.springContextLoaderSupport.customizeContext(servletContext,configurableWebApplicationContext);
   }
}
