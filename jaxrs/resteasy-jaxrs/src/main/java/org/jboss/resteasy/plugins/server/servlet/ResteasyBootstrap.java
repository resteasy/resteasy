package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
   private Registry registry;
   private Dispatcher dispatcher;


   public void contextInitialized(ServletContextEvent event)
   {
      ResteasyProviderFactory.setInstance(factory);

      event.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), factory);
      dispatcher = new SynchronousDispatcher(factory);
      registry = dispatcher.getRegistry();
      event.getServletContext().setAttribute(Dispatcher.class.getName(), dispatcher);
      event.getServletContext().setAttribute(Registry.class.getName(), registry);
      String applicationConfig = event.getServletContext().getInitParameter(Application.class.getName());

      String providers = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_PROVIDERS);

      if (providers != null) setProviders(providers);

      String resourceMethodInterceptors = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_RESOURCE_METHOD_INTERCEPTORS);

      if (resourceMethodInterceptors != null) setProviders(resourceMethodInterceptors);

      String builtin = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS);
      if (builtin == null || Boolean.valueOf(builtin.trim())) RegisterBuiltin.register(factory);

      boolean scanProviders = false;
      boolean scanResources = false;

      String sProviders = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_SCAN_PROVIDERS);
      if (sProviders != null)
      {
         scanProviders = Boolean.valueOf(sProviders.trim());
      }
      String scanAll = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_SCAN);
      if (scanAll != null)
      {
         boolean tmp = Boolean.valueOf(scanAll.trim());
         scanProviders = tmp || scanProviders;
         scanResources = tmp || scanResources;
      }
      String sResources = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_SCAN_RESOURCES);
      if (sResources != null)
      {
         scanResources = Boolean.valueOf(sResources.trim());
      }

      if (scanProviders || scanResources)
      {
         if (applicationConfig != null)
            throw new RuntimeException("You cannot deploy a javax.ws.rs.core.Application and have scanning on as this may create errors");

         URL[] urls = WarUrlFinder.findWebInfLibClasspaths(event);
         URL url = WarUrlFinder.findWebInfClassesPath(event);
         AnnotationDB db = new AnnotationDB();
         String[] ignoredPackages = {"org.jboss.resteasy.plugins", "org.jboss.resteasy.annotations", "org.jboss.resteasy.client", "org.jboss.resteasy.specimpl", "org.jboss.resteasy.core", "org.jboss.resteasy.spi", "org.jboss.resteasy.util", "org.jboss.resteasy.mock", "javax.ws.rs"};
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

      String jndiResources = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_JNDI_RESOURCES);
      if (jndiResources != null)
      {
         processJndiResources(jndiResources);
      }

      String resources = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_RESOURCES);
      if (resources != null)
      {
         processResources(resources);
      }

      // Mappings don't work anymore, but leaving the code in just in case users demand to put it back in
      String mimeExtentions = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_MEDIA_TYPE_MAPPINGS);
      if (mimeExtentions != null)
      {
         Map<String, String> map = parseMap(mimeExtentions);
         Map<String, MediaType> extMap = new HashMap<String, MediaType>();
         for (String ext : map.keySet())
         {
            String value = map.get(ext);
            extMap.put(ext, MediaType.valueOf(value));
         }
         if (dispatcher.getMediaTypeMappings() != null) dispatcher.getMediaTypeMappings().putAll(extMap);
         else dispatcher.setMediaTypeMappings(extMap);
      }

      // Mappings don't work anymore, but leaving the code in just in case users demand to put it back in
      String languageExtensions = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_LANGUAGE_MAPPINGS);
      if (languageExtensions != null)
      {
         Map<String, String> map = parseMap(languageExtensions);
         if (dispatcher.getLanguageMappings() != null) dispatcher.getLanguageMappings().putAll(map);
         else dispatcher.setLanguageMappings(map);
      }

      if (applicationConfig != null)
      {
         try
         {
            //System.out.println("application config: " + applicationConfig.trim());
            Class configClass = Thread.currentThread().getContextClassLoader().loadClass(applicationConfig.trim());
            Application config = (Application) configClass.newInstance();
            processApplication(config, registry, factory);
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
         catch (InstantiationException e)
         {
            throw new RuntimeException(e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
      }

   }

   public static void processApplication(Application config, Registry registry, ResteasyProviderFactory factory)
   {
      if (config.getClasses() != null)
      {
         for (Class clazz : config.getClasses())
         {
            if (clazz.isAnnotationPresent(Path.class)) registry.addPerRequestResource(clazz);
            else factory.registerProvider(clazz);
         }
      }
      if (config.getSingletons() != null)
      {
         for (Object obj : config.getSingletons())
         {
            if (obj.getClass().isAnnotationPresent(Path.class))
            {
               registry.addSingletonResource(obj);
            }
            else
            {
               factory.registerProviderInstance(obj);
            }
         }
      }
   }

   protected Map<String, String> parseMap(String map)
   {
      Map<String, String> parsed = new HashMap<String, String>();
      String[] entries = map.trim().split(",");
      for (String entry : entries)
      {
         String[] split = entry.trim().split(":");
         parsed.put(split[0].trim(), split[1].trim());

      }
      return parsed;
   }

   protected void processJndiResources(String jndiResources)
   {
      String[] resources = jndiResources.trim().split(",");
      for (String resource : resources)
      {
         registry.addJndiResource(resource.trim());
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
            registry.addPerRequestResource(clazz);
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
         registerProvider(clazz);
      }
   }

   private void registerProvider(String clazz)
   {
      Class provider = null;
      try
      {
         provider = Thread.currentThread().getContextClassLoader().loadClass(clazz);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
      System.out.println("FOUND JAX-RS @Provider: " + clazz);
      factory.registerProvider(provider);
   }

   protected void processResources(AnnotationDB db)
   {
      Set<String> classes = new HashSet<String>();
      Set<String> paths = db.getAnnotationIndex().get(Path.class.getName());
      if (paths != null) classes.addAll(paths);
      for (String clazz : classes)
      {
         processResource(clazz);
      }
   }

   protected void processResource(String clazz)
   {
      Class resource = null;
      try
      {
         resource = Thread.currentThread().getContextClassLoader().loadClass(clazz);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
      if (resource.isInterface()) return;
      if (GetRestful.isRootResource(resource) == false) return;

      System.out.println("FOUND JAX-RS resource: " + clazz);
      registry.addPerRequestResource(resource);
   }

   protected void setProviders(String providers)
   {
      String[] p = providers.split(",");
      for (String provider : p)
      {
         provider = provider.trim();
         registerProvider(provider);
      }
   }

   public void contextDestroyed(ServletContextEvent event)
   {
   }
}
