package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ListenerBootstrap extends ConfigurationBootstrap
{
   protected ServletContext servletContext;

   public ListenerBootstrap(final ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   private static Object RD_LOCK = new Object();

   @Override
   public ResteasyDeployment createDeployment()
   {
      ResteasyDeployment deployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
      if (deployment == null) deployment = super.createDeployment();
      deployment.getDefaultContextObjects().put(ResteasyDeployment.class, deployment);
      deployment.getDefaultContextObjects().put(ServletContext.class, servletContext);
      deployment.getDefaultContextObjects().put(ResteasyConfiguration.class, this);
      String servletMappingPrefix = getParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX);
      if (servletMappingPrefix == null) servletMappingPrefix = "";
      servletMappingPrefix = servletMappingPrefix.trim();

      synchronized (RD_LOCK)
      {
         @SuppressWarnings(value = "unchecked")
         Map<String, ResteasyDeployment> deployments = (Map<String, ResteasyDeployment>) servletContext.getAttribute(ResteasyContextParameters.RESTEASY_DEPLOYMENTS);
         if (deployments == null)
         {
            deployments = new ConcurrentHashMap<String, ResteasyDeployment>();
            servletContext.setAttribute("resteasy.deployments", deployments);
         }
         deployments.put(servletMappingPrefix, deployment);
      }
      return deployment;
   }

   public static URL[] findWebInfLibClasspaths(ServletContext servletContext)
   {
      ArrayList<URL> list = new ArrayList<URL>();
      Set libJars = servletContext.getResourcePaths("/WEB-INF/lib");
      if (libJars == null)
      {
         URL[] empty = {};
         return empty;
      }
      for (Object jar : libJars)
      {
         try
         {
            list.add(servletContext.getResource((String) jar));
         }
         catch (MalformedURLException e)
         {
            throw new RuntimeException(e);
         }
      }
      return list.toArray(new URL[list.size()]);
   }

   @Override
   public Set<String> getParameterNames()
   {
      Set<String> set = getResteasyConfigParameterNames();
      if (set != null)
      {
         return set;
      }
      return getServletContextNames();
   }

   protected Set<String> getServletContextNames()
   {
      Enumeration<String> en = servletContext.getInitParameterNames();
      HashSet<String> set = new HashSet<String>();
      while (en.hasMoreElements()) set.add(en.nextElement());
      return set;
   }

   @Override
   public Set<String> getInitParameterNames()
   {
      return getParameterNames();
   }

   public String getParameter(String name)
   {
      String val = resteasyConfig.getValue(name);
      if (val == null) val = servletContext.getInitParameter(name);
      return val;
   }

   @Override
   public String getInitParameter(String name)
   {
      return servletContext.getInitParameter(name);
   }

   protected Set<String> getResteasyConfigParameterNames()
   {
      Iterable<String> iterable = resteasyConfig.getPropertyNames();
      if (iterable == null)
      {
         return null;
      }
      Iterator<String> iterator = iterable.iterator();
      Set<String> set = new HashSet<String>();
      while (iterator.hasNext()) set.add(iterator.next());
      return set;
   }
}
