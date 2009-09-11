package org.jboss.resteasy.plugins.server.servlet;

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
