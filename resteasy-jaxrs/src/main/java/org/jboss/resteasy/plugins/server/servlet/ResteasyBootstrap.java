package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
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
   private final static Logger logger = LoggerFactory.getLogger(ResteasyBootstrap.class);
   protected ResteasyDeployment deployment = new ResteasyDeployment();


   public void contextInitialized(ServletContextEvent event)
   {
      String deploymentSensitive = event.getServletContext().getInitParameter("resteasy.use.deployment.sensitive.factory");
      if (deploymentSensitive != null)
         deployment.setDeploymentSensitiveFactoryEnabled(Boolean.valueOf(deploymentSensitive.trim()));
      else deployment.setDeploymentSensitiveFactoryEnabled(true);


      String async = event.getServletContext().getInitParameter("resteasy.async.job.service.enabled");
      if (async != null) deployment.setAsyncJobServiceEnabled(Boolean.valueOf(async.trim()));
      if (deployment.isAsyncJobServiceEnabled())
      {
         String maxJobResults = event.getServletContext().getInitParameter("resteasy.async.job.service.max.job.results");
         if (maxJobResults != null)
         {
            int maxJobs = Integer.valueOf(maxJobResults);
            deployment.setAsyncJobServiceMaxJobResults(maxJobs);
         }
         String maxWaitStr = event.getServletContext().getInitParameter("resteasy.async.job.service.max.wait");
         if (maxWaitStr != null)
         {
            long maxWait = Long.valueOf(maxWaitStr);
            deployment.setAsyncJobServiceMaxWait(maxWait);
         }
         String threadPool = event.getServletContext().getInitParameter("resteasy.async.job.service.thread.pool.size");
         if (threadPool != null)
         {
            int threadPoolSize = Integer.valueOf(threadPool);
            deployment.setAsyncJobServiceThreadPoolSize(threadPoolSize);
         }
         String basePath = event.getServletContext().getInitParameter("resteasy.async.job.service.base.path");
         if (basePath != null)
         {
            deployment.setAsyncJobServiceBasePath(basePath);
         }
      }
      String applicationConfig = event.getServletContext().getInitParameter(Application.class.getName());
      if (applicationConfig == null)
      {
         // stupid spec doesn't use FQN of Application class name
         applicationConfig = event.getServletContext().getInitParameter("javax.ws.rs.Application");
      }
      else
      {
         logger.warn("The use of " + Application.class.getName() + " is deprecated, please use javax.ws.rs.Application as a context-param instead");
      }

      String providers = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_PROVIDERS);

      if (providers != null)
      {
         String[] p = providers.split(",");
         for (String pr : p) deployment.getProviderClasses().add(pr.trim());
      }

      String resourceMethodInterceptors = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_RESOURCE_METHOD_INTERCEPTORS);

      if (resourceMethodInterceptors != null)
      {
         throw new RuntimeException(ResteasyContextParameters.RESTEASY_RESOURCE_METHOD_INTERCEPTORS + " is no longer a supported context param.  See documentation for more details");
      }

      String resteasySecurity = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_ROLE_BASED_SECURITY);
      if (resteasySecurity != null) deployment.setSecurityEnabled(Boolean.valueOf(resteasySecurity.trim()));

      String builtin = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS);
      if (builtin != null) deployment.setRegisterBuiltin(Boolean.valueOf(builtin.trim()));

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

      String mimeExtentions = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_MEDIA_TYPE_MAPPINGS);
      if (mimeExtentions != null)
      {
         Map<String, String> map = parseMap(mimeExtentions);
         deployment.setMediaTypeMappings(map);
      }

      String languageExtensions = event.getServletContext().getInitParameter(ResteasyContextParameters.RESTEASY_LANGUAGE_MAPPINGS);
      if (languageExtensions != null)
      {
         Map<String, String> map = parseMap(languageExtensions);
         deployment.setLanguageExtensions(map);
      }
      String before = event.getServletContext().getInitParameter("resteasy.interceptor.before.precedence");
      if (before != null)
      {
         Map<String, String> map = parseMap(before);
         deployment.setInterceptorBeforePrecedences(map);
      }
      String after = event.getServletContext().getInitParameter("resteasy.interceptor.after.precedence");
      if (after != null)
      {
         Map<String, String> map = parseMap(after);
         deployment.setInterceptorAfterPrecedences(map);
      }
      String append = event.getServletContext().getInitParameter("resteasy.append.interceptor.precedence");
      if (append != null)
      {
         String[] precedences = append.split(",");
         for (String precedence : precedences)
         {
            deployment.getInterceptorPrecedences().add(precedence.trim());
         }
      }

      if (applicationConfig != null) deployment.setApplicationClass(applicationConfig);

      deployment.start();

      event.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
      event.getServletContext().setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
      event.getServletContext().setAttribute(Registry.class.getName(), deployment.getRegistry());

   }

   public static void processApplication(Application config, Registry registry, ResteasyProviderFactory factory)
   {
      logger.info("Deploying " + Application.class.getName() + ": " + config.getClass());
      if (config.getClasses() != null)
      {
         for (Class clazz : config.getClasses())
         {
            if (clazz.isAnnotationPresent(Path.class))
            {
               logger.info("Adding class resource " + clazz.getName() + " from Application " + Application.class.getName());
               registry.addPerRequestResource(clazz);
            }
            else
            {
               logger.info("Adding class @Provider " + clazz.getName() + " from Application " + Application.class.getName());
               factory.registerProvider(clazz);
            }
         }
      }
      if (config.getSingletons() != null)
      {
         for (Object obj : config.getSingletons())
         {
            if (obj.getClass().isAnnotationPresent(Path.class))
            {
               logger.info("Adding singleton resource " + obj.getClass().getName() + " from Application " + Application.class.getName());
               registry.addSingletonResource(obj);
            }
            else
            {
               logger.info("Adding singleton @Provider " + obj.getClass().getName() + " from Application " + Application.class.getName());
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
         deployment.getJndiResources().add(resource);
      }
   }

   protected void processResources(String list)
   {
      String[] resources = list.trim().split(",");
      for (String resource : resources)
      {
         deployment.getResourceClasses().add(resource);
      }
   }

   protected void processProviders(AnnotationDB db)
   {
      Set<String> classes = db.getAnnotationIndex().get(Provider.class.getName());
      if (classes == null) return;
      for (String clazz : classes)
      {
         logger.info("Adding scanned @Provider: " + clazz);
         deployment.getProviderClasses().add(clazz);
      }
   }

   protected void processResources(AnnotationDB db)
   {
      Set<String> classes = new HashSet<String>();
      Set<String> paths = db.getAnnotationIndex().get(Path.class.getName());
      if (paths != null) classes.addAll(paths);
      for (String clazz : classes)
      {
         logger.info("Adding scanned resource: " + clazz);
         deployment.getResourceClasses().add(clazz);
      }
   }

   public void contextDestroyed(ServletContextEvent event)
   {
      deployment.stop();
   }
}
