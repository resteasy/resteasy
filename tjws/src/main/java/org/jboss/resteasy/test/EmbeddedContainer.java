package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated See resteasy-undertow module.
 */
@Deprecated
public class EmbeddedContainer
{
   private static Class<?> bootstrap = TJWSServletContainer.class;

   public static Class getBootstrap()
   {
      return bootstrap;
   }

   /**
    * Exists for tests that require a servlet container behind the scenes.
    *
    * @return true
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

   public static ResteasyDeployment start() throws Exception
   {
      return start("/", (Hashtable<String,String>) null);
   }
   
   public static ResteasyDeployment start(String bindPath) throws Exception
   {
      return start(bindPath, null, null);
   }
   
   public static ResteasyDeployment start(Hashtable<String,String> initParams) throws Exception
   {
      return start("/", initParams);
   }

   public static ResteasyDeployment start(Hashtable<String,String> initParams, Hashtable<String,String> contextParams) throws Exception
   {
      return start("/", initParams, contextParams);
   }
   
   public static ResteasyDeployment start(String bindPath, Hashtable<String,String> initParams) throws Exception
   {
      Method start = bootstrap.getMethod("start", String.class, Hashtable.class);
      return (ResteasyDeployment) start.invoke(null, bindPath, initParams);
   }

   public static ResteasyDeployment start(String bindPath, Hashtable<String,String> initParams, Hashtable<String,String> contextParams) throws Exception
   {
      Method start = bootstrap.getMethod("start", String.class, Hashtable.class, Hashtable.class);
      return (ResteasyDeployment) start.invoke(null, bindPath, initParams, contextParams);
   }
   
   public static ResteasyDeployment start(String bindPath, SecurityDomain domain) throws Exception
   {
      Method start = bootstrap.getMethod("start", String.class, SecurityDomain.class);
      return (ResteasyDeployment) start.invoke(null, bindPath, domain);

   }

   public static void start(ResteasyDeployment deployment) throws Exception
   {
      Method start = bootstrap.getMethod("start", ResteasyDeployment.class);
      start.invoke(null, deployment);

   }

   public static void stop() throws Exception
   {
      Method stop = bootstrap.getMethod("stop");
      stop.invoke(null);
   }
}