package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.ServletConfig;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletBootstrap extends ListenerBootstrap
{
   private ServletConfig config;

   public ServletBootstrap(ServletConfig config)
   {
      super(config.getServletContext());
      this.config = config;
   }

   @Override
   public ResteasyDeployment createDeployment()
   {
      ResteasyDeployment deployment = super.createDeployment();
      deployment.getDefaultContextObjects().put(ServletConfig.class, config);
      return deployment;
   }


   public String getParameter(String name)
   {
      String val = config.getInitParameter(name);
      if (val == null) val = super.getParameter(name);
      return val;
   }
}