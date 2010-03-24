package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.scannotation.WarUrlFinder;

import javax.servlet.ServletContext;
import java.net.URL;

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

   public URL[] getScanningUrls()
   {
      URL[] urls = WarUrlFinder.findWebInfLibClasspaths(servletContext);
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
}
