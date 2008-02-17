package org.resteasy.plugins.server.servlet;

import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.resourcefactory.JndiResourceFactory;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a ServletContextListener that creates the registry for resteasy and stuffs it as a servlet context attribute
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyBootstrap implements ServletContextListener
{
   private ResteasyProviderFactory factory = new ResteasyProviderFactory();
   private Registry registry = new Registry(factory);

   public void contextInitialized(ServletContextEvent event)
   {
      ResteasyProviderFactory.setInstance(factory);

      event.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), factory);
      event.getServletContext().setAttribute(Registry.class.getName(), registry);

      String providers = event.getServletContext().getInitParameter("resteasy.providers");

      if (providers != null) setProviders(providers);
      else RegisterBuiltin.register(factory);

      boolean scanProviders = false;
      boolean scanResources = false;

      String sProviders = event.getServletContext().getInitParameter("resteasy.scan.providers");
      if (sProviders != null)
      {
         scanProviders = Boolean.valueOf(sProviders.trim());
      }
      String scanAll = event.getServletContext().getInitParameter("resteasy.scan");
      if (scanAll != null)
      {
         boolean tmp = Boolean.valueOf(scanAll.trim());
         scanProviders = tmp || scanProviders;
         scanResources = tmp || scanResources;
      }
      String sResources = event.getServletContext().getInitParameter("resteasy.scan.resources");
      if (sResources != null)
      {
         scanResources = Boolean.valueOf(sResources.trim());
      }

      if (scanProviders || scanResources)
      {
         URL[] urls = WarUrlFinder.findWebInfLibClasspaths(event);
         URL url = WarUrlFinder.findWebInfClassesPath(event);
         AnnotationDB db = new AnnotationDB();
         String[] ignoredPackages = {"org.resteasy.plugins", "javax.ws.rs"};
         db.setIgnoredPackages(ignoredPackages);
         try
         {
            if (url != null) db.scanArchives(url);
            db.scanArchives(urls);
            try
            {
               db.crossReferenceImplementedInterfaces();
               db.crossReferenceMetaAnnotations();
            }
            catch (AnnotationDB.CrossReferenceException ignored)
            {

            }

         }
         catch (IOException e)
         {
            throw new RuntimeException("Unable to scan WEB-INF for JAX-RS annotations, you must manually register your classes/resources", e);
         }

         if (scanProviders) processProviders(db);
         if (scanResources) processResources(db);
      }

      String jndiResources = event.getServletContext().getInitParameter("resteasy.jndi.resources");
      if (jndiResources != null)
      {
         processJndiResources(jndiResources);
      }

      String resources = event.getServletContext().getInitParameter("resteasy.resources");
      if (resources != null)
      {
         processResources(resources);
      }
   }

   protected void processJndiResources(String jndiResources)
   {
      String[] resources = jndiResources.trim().split(",");
      for (String resource : resources)
      {
         registry.addResourceFactory(new JndiResourceFactory(resource.trim()));
      }
   }

   protected void processResources(String list)
   {
      String[] resources = list.trim().split(",");
      for (String resource : resources)
      {
         try
         {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(resource.trim());
            registry.addResource(clazz);
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   protected void processProviders(AnnotationDB db)
   {
      Set<String> classes = db.getAnnotationIndex().get(Provider.class.getName());
      if (classes == null) return;
      for (String clazz : classes)
      {
         System.out.println("FOUND JAX-RS @Provider: " + clazz);
         Class provider = null;
         try
         {
            provider = Thread.currentThread().getContextClassLoader().loadClass(clazz);
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
         if (MessageBodyReader.class.isAssignableFrom(provider))
         {
            try
            {
               factory.addMessageBodyReader((MessageBodyReader) provider.newInstance());
            }
            catch (Exception e)
            {
               throw new RuntimeException("Unable to instantiate MessageBodyReader", e);
            }
         }
         if (MessageBodyWriter.class.isAssignableFrom(provider))
         {
            try
            {
               factory.addMessageBodyWriter((MessageBodyWriter) provider.newInstance());
            }
            catch (Exception e)
            {
               throw new RuntimeException("Unable to instantiate MessageBodyWriter", e);
            }
         }
      }
   }

   protected void processResources(AnnotationDB db)
   {
      Set<String> classes = new HashSet<String>();
      Set<String> paths = db.getAnnotationIndex().get(Path.class.getName());
      if (paths != null) classes.addAll(paths);
      paths = db.getAnnotationIndex().get(HttpMethod.class.getName());
      if (paths != null) classes.addAll(paths);
      for (String clazz : classes)
      {
         System.out.println("FOUND JAX-RS resource: " + clazz);
         Class resource = null;
         try
         {
            resource = Thread.currentThread().getContextClassLoader().loadClass(clazz);
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
         if (resource.isInterface()) continue;
         registry.addResourceFactory(new POJOResourceFactory(resource));
      }
   }

   protected void setProviders(String providers)
   {
      String[] p = providers.split(",");
      for (String provider : p)
      {
         provider = provider.trim();
         Object obj = null;
         try
         {
            Class prov = Thread.currentThread().getContextClassLoader().loadClass(provider);
            obj = prov.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         if (obj instanceof MessageBodyReader) factory.addMessageBodyReader((MessageBodyReader) obj);
         if (obj instanceof MessageBodyWriter) factory.addMessageBodyWriter((MessageBodyWriter) obj);
      }
   }

   public void contextDestroyed(ServletContextEvent event)
   {
   }
}
