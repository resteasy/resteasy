package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.plugins.spring.i18n.Messages;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringContextLoaderListener extends ContextLoaderListener
{
   private SpringContextLoaderSupport springContextLoaderSupport = new SpringContextLoaderSupport();

   public SpringContextLoaderListener() {
   }

   public SpringContextLoaderListener(final WebApplicationContext context) {
      super(context);
   }

   @Override
   public void contextInitialized(ServletContextEvent event)
   {
      boolean scanProviders = false;
      boolean scanResources = false;

      String sProviders = event.getServletContext().getInitParameter("resteasy.scan.providers");
      if (sProviders != null)
      {
         scanProviders = Boolean.valueOf(sProviders.trim());
      }
      String scanAll = event.getServletContext().getInitParameter("resteasy.scan");
      if (scanAll != null)
      {
         boolean tmp = Boolean.valueOf(scanAll.trim());
         scanProviders = tmp || scanProviders;
         scanResources = tmp;
      }
      String sResources = event.getServletContext().getInitParameter("resteasy.scan.resources");
      if (sResources != null)
      {
         scanResources = Boolean.valueOf(sResources.trim());
      }

      if (scanProviders || scanResources)
      {
         throw new RuntimeException(Messages.MESSAGES.cannotUseScanParameters());
      }


      super.contextInitialized(event);
   }

   @Deprecated
   protected ContextLoader createContextLoader()
   {
      return new SpringContextLoader();
   }

   @Override
   protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext) {
      super.customizeContext(servletContext, configurableWebApplicationContext);
      this.springContextLoaderSupport.customizeContext(servletContext, configurableWebApplicationContext);
   }
}
