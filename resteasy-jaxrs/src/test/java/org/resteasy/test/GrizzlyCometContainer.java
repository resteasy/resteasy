package org.resteasy.test;

import org.resteasy.Dispatcher;
import org.resteasy.ResourceMethodRegistry;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.grizzly.GrizzletDispatcher;
import org.resteasy.plugins.server.grizzly.GrizzlyCometServer;
import org.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyCometContainer
{
   private static GrizzlyCometServer server = null;
   private static GrizzletDispatcher grizzlet;

   public static Dispatcher start(String bindPath) throws Exception
   {
      long start = System.currentTimeMillis();
      ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory.initializeInstance());
      grizzlet = new GrizzletDispatcher(ResteasyProviderFactory.getInstance(), registry, bindPath);

      server = new GrizzlyCometServer();
      server.setPort(8081);
      server.setGrizzlet(grizzlet);
      server.start();
      long end = System.currentTimeMillis() - start;
      System.out.println("Grizzly Comet start time: " + end);

      RegisterBuiltin.register(grizzlet.getDispatcher().getProviderFactory());
      return grizzlet.getDispatcher();
   }

   public static void stop() throws Exception
   {
      server.stop();
   }
}