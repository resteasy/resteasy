package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.scannotation.AnnotationDB;

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
 * Create a deployment from String-based configuration data
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
abstract public class ConfigurationBootstrap implements ResteasyConfiguration
{
   private static Logger logger = null;
   private ResteasyDeployment deployment = new ResteasyDeployment();

   public abstract URL[] getScanningUrls();


   public ResteasyDeployment createDeployment()
   {
      String loggerTypeString = getParameter("resteasy.logger.type");
      if (loggerTypeString != null)
      {
         Logger.LoggerType loggerType = Logger.LoggerType.valueOf(loggerTypeString);
         Logger.setLoggerType(loggerType);

      }
      logger = Logger.getLogger(ConfigurationBootstrap.class);
      String deploymentSensitive = getParameter("resteasy.use.deployment.sensitive.factory");
      if (deploymentSensitive != null)
         deployment.setDeploymentSensitiveFactoryEnabled(Boolean.valueOf(deploymentSensitive.trim()));
      else deployment.setDeploymentSensitiveFactoryEnabled(true);


      String async = getParameter("resteasy.async.job.service.enabled");
      if (async != null) deployment.setAsyncJobServiceEnabled(Boolean.valueOf(async.trim()));
      if (deployment.isAsyncJobServiceEnabled())
      {
         String maxJobResults = getParameter("resteasy.async.job.service.max.job.results");
         if (maxJobResults != null)
         {
            int maxJobs = Integer.valueOf(maxJobResults);
            deployment.setAsyncJobServiceMaxJobResults(maxJobs);
         }
         String maxWaitStr = getParameter("resteasy.async.job.service.max.wait");
         if (maxWaitStr != null)
         {
            long maxWait = Long.valueOf(maxWaitStr);
            deployment.setAsyncJobServiceMaxWait(maxWait);
         }
         String threadPool = getParameter("resteasy.async.job.service.thread.pool.size");
         if (threadPool != null)
         {
            int threadPoolSize = Integer.valueOf(threadPool);
            deployment.setAsyncJobServiceThreadPoolSize(threadPoolSize);
         }
         String basePath = getParameter("resteasy.async.job.service.base.path");
         if (basePath != null)
         {
            deployment.setAsyncJobServiceBasePath(basePath);
         }
      }
      String applicationConfig = getParameter(Application.class.getName());
      if (applicationConfig == null)
      {
         // stupid spec doesn't use FQN of Application class name
         applicationConfig = getParameter("javax.ws.rs.Application");
      }
      else
      {
         logger.warn("The use of " + Application.class.getName() + " is deprecated, please use javax.ws.rs.Application as a context-param instead");
      }

      String providers = getParameter(ResteasyContextParameters.RESTEASY_PROVIDERS);

      if (providers != null && ! "".equals(providers.trim()))
      {
         String[] p = providers.split(",");
         for (String pr : p) deployment.getProviderClasses().add(pr.trim());
      }


      String resourceMethodInterceptors = getParameter(ResteasyContextParameters.RESTEASY_RESOURCE_METHOD_INTERCEPTORS);

      if (resourceMethodInterceptors != null)
      {
         throw new RuntimeException(ResteasyContextParameters.RESTEASY_RESOURCE_METHOD_INTERCEPTORS + " is no longer a supported context param.  See documentation for more details");
      }

      String resteasySecurity = getParameter(ResteasyContextParameters.RESTEASY_ROLE_BASED_SECURITY);
      if (resteasySecurity != null) deployment.setSecurityEnabled(Boolean.valueOf(resteasySecurity.trim()));

      String builtin = getParameter(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS);
      if (builtin != null) deployment.setRegisterBuiltin(Boolean.valueOf(builtin.trim()));

      boolean scanProviders = false;
      boolean scanResources = false;

      String sProviders = getParameter(ResteasyContextParameters.RESTEASY_SCAN_PROVIDERS);
      if (sProviders != null)
      {
         scanProviders = parseBooleanParam(ResteasyContextParameters.RESTEASY_SCAN_PROVIDERS, sProviders);
      }
      String scanAll = getParameter(ResteasyContextParameters.RESTEASY_SCAN);
      if (scanAll != null)
      {
         boolean tmp = parseBooleanParam(ResteasyContextParameters.RESTEASY_SCAN, scanAll);
         scanProviders = tmp || scanProviders;
         scanResources = tmp || scanResources;
      }
      String sResources = getParameter(ResteasyContextParameters.RESTEASY_SCAN_RESOURCES);
      if (sResources != null)
      {
         scanResources = parseBooleanParam(ResteasyContextParameters.RESTEASY_SCAN_RESOURCES, sResources);
      }

      // Check to see if scanning is being done by deployer (i.e. JBoss App Server)
      String sScannedByDeployer = getParameter(ResteasyContextParameters.RESTEASY_SCANNED_BY_DEPLOYER);
      if (sScannedByDeployer != null)
      {
         boolean tmp = parseBooleanParam(ResteasyContextParameters.RESTEASY_SCANNED_BY_DEPLOYER, sScannedByDeployer);
         if (tmp)
         {
            scanProviders = false;
            scanResources = false;
         }
      }

      if (scanProviders || scanResources)
      {
         logger.debug("Scanning...");

         URL[] urls = getScanningUrls();
         for (URL u : urls)
         {
            logger.debug("Scanning archive: " + u);
         }
         AnnotationDB db = new AnnotationDB();
         String[] ignoredPackages = {"org.jboss.resteasy.plugins", "org.jboss.resteasy.annotations", "org.jboss.resteasy.client", "org.jboss.resteasy.specimpl", "org.jboss.resteasy.core", "org.jboss.resteasy.spi", "org.jboss.resteasy.util", "org.jboss.resteasy.mock", "javax.ws.rs"};
         db.setIgnoredPackages(ignoredPackages);

         // only index class annotations as we don't want sub-resources being picked up in the scan
         db.setScanClassAnnotations(true);
         db.setScanFieldAnnotations(false);
         db.setScanMethodAnnotations(false);
         db.setScanParameterAnnotations(false);
         try
         {
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

         if (scanProviders) processScannedProviders(db);
         if (scanResources) processScannedResources(db);
      }


      String scannedProviders = getParameter(ResteasyContextParameters.RESTEASY_SCANNED_PROVIDERS);

      if (scannedProviders != null)
      {
         String[] p = scannedProviders.split(",");
         for (String pr : p) deployment.getScannedProviderClasses().add(pr.trim());
      }

      String scannedResources = getParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES);

      if (scannedResources != null)
      {
         String[] p = scannedResources.split(",");
         for (String pr : p) deployment.getScannedResourceClasses().add(pr.trim());
      }

      String scannedJndi = getParameter(ResteasyContextParameters.RESTEASY_SCANNED_JNDI_RESOURCES);

      if (scannedJndi != null)
      {
         processScannedJndiComponentResources(scannedJndi);
      }


      String jndiResources = getParameter(ResteasyContextParameters.RESTEASY_JNDI_RESOURCES);
      if (jndiResources != null && ! "".equals(jndiResources.trim()))
      {
         processJndiResources(jndiResources);
      }

      String jndiComponentResources = getParameter(ResteasyContextParameters.RESTEASY_JNDI_COMPONENT_RESOURCES);
      if (jndiComponentResources != null)
      {
         processJndiComponentResources(jndiComponentResources);
      }

      String resources = getParameter(ResteasyContextParameters.RESTEASY_RESOURCES);
      if (resources != null && ! "".equals(resources.trim()))
      {
         processResources(resources);
      }

      String unwrapped = getParameter(ResteasyContextParameters.RESTEASY_UNWRAPPED_EXCEPTIONS);
      if (unwrapped != null)
      {
         processUnwrapped(unwrapped);
      }

      String paramMapping = getParameter(ResteasyContextParameters.RESTEASY_MEDIA_TYPE_PARAM_MAPPING);
      if (paramMapping != null)
      {
         paramMapping = paramMapping.trim();

         if (paramMapping.length() > 0)
         {
            deployment.setMediaTypeParamMapping(paramMapping);
         }
         else
         {
            deployment.setMediaTypeParamMapping(HttpHeaderNames.ACCEPT.toLowerCase());
         }
      }

      String contextObjects = getParameter(ResteasyContextParameters.RESTEASY_CONTEXT_OBJECTS);
      if (contextObjects != null)
      {
         Map<String, String> map = parseMap(contextObjects);
         deployment.setConstructedDefaultContextObjects(map);
      }

      String mimeExtentions = getParameter(ResteasyContextParameters.RESTEASY_MEDIA_TYPE_MAPPINGS);
      if (mimeExtentions != null)
      {
         Map<String, String> map = parseMap(mimeExtentions);
         deployment.setMediaTypeMappings(map);
      }

      String languageExtensions = getParameter(ResteasyContextParameters.RESTEASY_LANGUAGE_MAPPINGS);
      if (languageExtensions != null)
      {
         Map<String, String> map = parseMap(languageExtensions);
         deployment.setLanguageExtensions(map);
      }
      String before = getParameter(ResteasyContextParameters.RESTEASY_INTERCEPTOR_BEFORE_PRECEDENCE);
      if (before != null)
      {
         Map<String, String> map = parseMap(before);
         deployment.setInterceptorBeforePrecedences(map);
      }
      String after = getParameter(ResteasyContextParameters.RESTEASY_INTERCEPTOR_AFTER_PRECEDENCE);
      if (after != null)
      {
         Map<String, String> map = parseMap(after);
         deployment.setInterceptorAfterPrecedences(map);
      }
      String append = getParameter(ResteasyContextParameters.RESTEASY_APPEND_INTERCEPTOR_PRECEDENCE);
      if (append != null)
      {
         String[] precedences = append.split(",");
         for (String precedence : precedences)
         {
            deployment.getInterceptorPrecedences().add(precedence.trim());
         }
      }

      String useContainerFormParams = getParameter(ResteasyContextParameters.USE_CONTAINER_FORM_PARAMS);
      if (useContainerFormParams != null)
      {
         boolean useContainer = parseBooleanParam(ResteasyContextParameters.USE_CONTAINER_FORM_PARAMS, useContainerFormParams);
         deployment.setUseContainerFormParams(useContainer);
      }


      String injectorFactoryClass = getParameter("resteasy.injector.factory");
      if (injectorFactoryClass != null)
      {
         deployment.setInjectorFactoryClass(injectorFactoryClass);
      }

      if (applicationConfig != null) deployment.setApplicationClass(applicationConfig);
      deployment.getDefaultContextObjects().put(ResteasyConfiguration.class, this);
      return deployment;
   }

    private boolean parseBooleanParam(String key, String value) {
        value = value.trim().toLowerCase();
        if (value.equals("true") || value.equals("1")) {
            return true;
        } else if (value.equals("false") || value.equals("0")) {
            return false;
        } else {
            throw new RuntimeException("The " + key + " config in web.xml could not be parsed, accepted values are true,false or 1,0");

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

   protected void processJndiComponentResources(String jndiResources)
   {
      String[] resources = jndiResources.trim().split(",");
      for (String resource : resources)
      {
         deployment.getJndiComponentResources().add(resource);
      }
   }

   protected void processScannedJndiComponentResources(String jndiResources)
   {
      String[] resources = jndiResources.trim().split(",");
      for (String resource : resources)
      {
         deployment.getScannedJndiComponentResources().add(resource);
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

   protected void processUnwrapped(String list)
   {
      String[] resources = list.trim().split(",");
      for (String resource : resources)
      {
         deployment.getUnwrappedExceptions().add(resource);
      }
   }

   protected void processScannedProviders(AnnotationDB db)
   {
      Set<String> classes = db.getAnnotationIndex().get(Provider.class.getName());
      if (classes == null) return;
      for (String clazz : classes)
      {
         logger.info("Adding scanned @Provider: " + clazz);
         deployment.getScannedProviderClasses().add(clazz);
      }
   }

   protected void processScannedResources(AnnotationDB db)
   {
      Set<String> classes = new HashSet<String>();
      Set<String> paths = db.getAnnotationIndex().get(Path.class.getName());
      if (paths != null) classes.addAll(paths);
      for (String clazz : classes)
      {
         Class cls = null;
         try
         {
            // Ignore interfaces and subresource classes
            // Scanning is different than other deployment methods
            // in other deployment methods we don't want to ignore interfaces and subresources as they are
            // application errors
            cls = Thread.currentThread().getContextClassLoader().loadClass(clazz.trim());
            if (cls.isInterface()) continue;
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }

         logger.info("Adding scanned resource: " + clazz);
         deployment.getScannedResourceClasses().add(clazz);
      }
   }
}
