package org.jboss.resteasy.plugins.server.servlet;

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

   public String getParameter(String name)
   {
      String val = config.getInitParameter(name);
      if (val == null) val = super.getParameter(name);
      return val;
   }
}