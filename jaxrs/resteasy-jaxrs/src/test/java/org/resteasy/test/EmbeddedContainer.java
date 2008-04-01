package org.resteasy.test;

import org.resteasy.Dispatcher;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmbeddedContainer
{
   private static Class bootstrap = TJWSServletContainer.class;

   public static Class getBootstrap()
   {
      return bootstrap;
   }

   /**
    * Exists for tests that require a servlet container behind the scenes.
    *
    * @return
    */
   public static boolean isServlet()
   {
      return true;
   }

   static
   {
      String boot = System.getProperty("org.resteasy.test.embedded.container");
      if (boot != null)
      {
         try
         {
            bootstrap = Thread.currentThread().getContextClassLoader().loadClass(boot);
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public static void setBootstrap(Class bootstrap)
   {
      EmbeddedContainer.bootstrap = bootstrap;
   }

   public static Dispatcher start() throws Exception
   {
      return start("/");
   }

   public static Dispatcher start(String bindPath) throws Exception
   {
      Method start = bootstrap.getMethod("start", String.class);
      return (Dispatcher) start.invoke(null, bindPath);
   }

   public static void stop() throws Exception
   {
      Method stop = bootstrap.getMethod("stop");
      stop.invoke(null);
   }
}
