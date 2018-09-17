package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This is a ServletContextListener that creates the registry for resteasy and stuffs it as a servlet context attribute
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyBootstrap implements ServletContextListener
{
   protected ResteasyDeployment deployment;

   public void contextInitialized(ServletContextEvent event)
   {
      ServletContext servletContext = event.getServletContext();

      ListenerBootstrap config = new ListenerBootstrap(event.getServletContext());
      deployment = config.createDeployment();
      deployment.start();

      servletContext.setAttribute(ResteasyDeployment.class.getName(), deployment);
   }

   public void contextDestroyed(ServletContextEvent event)
   {
      deployment.stop();
   }

}
