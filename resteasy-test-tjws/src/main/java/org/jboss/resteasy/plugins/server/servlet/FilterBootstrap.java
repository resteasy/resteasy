package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FilterBootstrap extends ListenerBootstrap
{
   private FilterConfig config;

   public FilterBootstrap(FilterConfig config)
   {
      super(config.getServletContext());
      this.config = config;
   }

   @Override
   public ResteasyDeployment createDeployment()
   {
      ResteasyDeployment deployment = super.createDeployment();
      deployment.getDefaultContextObjects().put(FilterConfig.class, config);
      deployment.getDefaultContextObjects().put(ServletContext.class, config.getServletContext());
      return deployment;
   }

   public String getParameter(String name)
   {
      String val = config.getInitParameter(name);
      if (val == null) val = super.getParameter(name);
      return val;
   }

   @Override
   public String getInitParameter(String name)
   {
      return config.getInitParameter(name);
   }

   @Override
   public Set<String> getParameterNames()
   {
      Set<String> set = super.getInitParameterNames();
      Enumeration<String> en = config.getInitParameterNames();
      while (en.hasMoreElements()) set.add(en.nextElement());
      return set;
   }

   @Override
   public Set<String> getInitParameterNames()
   {
      Set<String> set = new HashSet<String>();
      Enumeration<String> en = config.getInitParameterNames();
      while (en.hasMoreElements()) set.add(en.nextElement());
      return set;
   }
}