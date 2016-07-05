package org.jboss.resteasy.plugins.spring;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringContextLoader extends ContextLoader
{
   private SpringContextLoaderSupport springContextLoaderSupport = new SpringContextLoaderSupport();

   protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext)
   {
      super.customizeContext(servletContext, configurableWebApplicationContext);
      this.springContextLoaderSupport.customizeContext(servletContext,configurableWebApplicationContext);
   }
}
