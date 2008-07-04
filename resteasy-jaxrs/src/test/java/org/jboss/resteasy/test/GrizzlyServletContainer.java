package org.jboss.resteasy.test;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.grizzly.GrizzlyHttpServletServer;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyServletContainer
{
   private static GrizzlyHttpServletServer server = null;
   private static HttpServletDispatcher servlet = new HttpServletDispatcher();

   public static Dispatcher start(String bindPath) throws Exception
   {
      long start = System.currentTimeMillis();
      server = new GrizzlyHttpServletServer();
      server.setPort(8081);
      server.setServlet(servlet);
      server.setServletPath(bindPath);
      server.start();
      long end = System.currentTimeMillis() - start;
      System.out.println("Grizzly Servlet start time: " + end);

      ResteasyProviderFactory.setInstance(servlet.getDispatcher().getProviderFactory());
      RegisterBuiltin.register(servlet.getDispatcher().getProviderFactory());
      return servlet.getDispatcher();
   }

   public static void stop() throws Exception
   {
      server.stop();
   }
}