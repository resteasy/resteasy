package org.jboss.resteasy.plugins.validation.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 13, 2014
 */
@WebListener
@ApplicationScoped
public class ContextProducer implements ServletContextListener
{
   private static ServletContext context;

   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      context = sce.getServletContext();
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      // Blank
   }

   @Produces
   @ResteasyValidationCDIProducerBinding
   private ServletContext getContext()
   {
      return context;
   }
}