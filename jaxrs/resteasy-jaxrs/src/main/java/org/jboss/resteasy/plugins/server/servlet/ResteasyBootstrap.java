package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
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
import javax.ws.rs.core.ApplicationConfig;
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
      dispatcher = new SynchronousDispatcher();
      dispatcher.setProviderFactory(factory);
      registry = dispatcher.getRegistry();
      String rootPath = event.getServletContext().getInitParameter("resteasy.servlet.mapping.prefix");
      if (rootPath != null) ((ResourceMethodRegistry) registry).setRootPath(rootPath.trim());
      event.getServletContext().setAttribute(Dispatcher.class.getName(), dispatcher);
      event.getServletContext().setAttribute(Registry.class.getName(), registry);

      String providers = event.getServletContext().getInitParameter("resteasy.providers");

      if (providers != null) setProviders(providers);

      String builtin = event.getServletContext().getInitParameter("resteasy.use.builtin.providers");
      if (builtin == null || Boolean.valueOf(builtin.trim())) RegisterBuiltin.register(factory);

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
         String[] ignoredPackages = {"org.jboss.resteasy.plugins", "javax.ws.rs"};
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

      String mimeExtentions = event.getServletContext().getInitParameter("resteasy.media.type.mappings");
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

      String languageExtensions = event.getServletContext().getInitParameter("resteasy.language.mappings");
      if (languageExtensions != null)
      {
         Map<String, String> map = parseMap(languageExtensions);
         if (dispatcher.getLanguageMappings() != null) dispatcher.getLanguageMappings().putAll(map);
         else dispatcher.setLanguageMappings(map);
      }

      String applicationConfig = event.getServletContext().getInitParameter(ApplicationConfig.class.getName());
      if (applicationConfig != null)
      {
         try
         {
            //System.out.println("application config: " + applicationConfig.trim());
            Class configClass = Thread.currentThread().getContextClassLoader().loadClass(applicationConfig.trim());
            ApplicationConfig config = (ApplicationConfig) configClass.newInstance();
            if (config.getLanguageMappings() != null)
            {
               if (dispatcher.getLanguageMappings() != null)
                  dispatcher.getLanguageMappings().putAll(config.getLanguageMappings());
               else dispatcher.setLanguageMappings(config.getLanguageMappings());
            }
            if (config.getMediaTypeMappings() != null)
            {
               if (dispatcher.getMediaTypeMappings() != null)
                  dispatcher.getMediaTypeMappings().putAll(config.getMediaTypeMappings());
               else dispatcher.setMediaTypeMappings(config.getMediaTypeMappings());
            }
            if (config.getResourceClasses() != null)
               for (Class clazz : config.getResourceClasses()) registry.addPerRequestResource(clazz);
            if (config.getProviderClasses() != null)
               for (Class clazz : config.getProviderClasses()) factory.registerProvider(clazz);
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
