package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.scannotation.WarUrlFinder;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ListenerBootstrap extends ConfigurationBootstrap
{
   protected ServletContext servletContext;

   public ListenerBootstrap(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   @Override
   public ResteasyDeployment createDeployment()
   {
      ResteasyDeployment deployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
      if (deployment == null) deployment = super.createDeployment();
      deployment.getDefaultContextObjects().put(ServletContext.class, servletContext);
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

   public URL[] getScanningUrls()
   {
      URL[] urls = findWebInfLibClasspaths(servletContext);
      URL url = WarUrlFinder.findWebInfClassesPath(servletContext);
      if (url == null) return urls;
      URL[] all = new URL[urls.length + 1];
      int i = 0;
      for (i = 0; i < urls.length; i++)
      {
         all[i] = urls[i];
      }
      all[i] = url;
      return all;
   }

   public String getParameter(String name)
   {
      return servletContext.getInitParameter(name);
   }

   @Override
   public String getInitParameter(String name)
   {
      return servletContext.getInitParameter(name);
   }
}
