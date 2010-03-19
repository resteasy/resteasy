package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.AcceptParameterHttpPreprocessor;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.interceptors.SecurityInterceptor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to configure and initialize the core components of RESTEasy.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyDeployment
{
   protected boolean deploymentSensitiveFactoryEnabled = false;
   protected boolean asyncJobServiceEnabled = false;
   protected int asyncJobServiceMaxJobResults = 100;
   protected long asyncJobServiceMaxWait = 300000;
   protected int asyncJobServiceThreadPoolSize = 100;
   protected String asyncJobServiceBasePath = "/asynch/jobs";
   protected String applicationClass;
   protected Application application;
   protected boolean registerBuiltin = true;
   protected List<String> providerClasses = new ArrayList<String>();
   protected List<Class> actualProviderClasses = new ArrayList<Class>();
   protected List<Object> providers = new ArrayList<Object>();
   protected boolean securityEnabled = false;
   protected List<String> jndiResources = new ArrayList<String>();
   protected List<String> resourceClasses = new ArrayList<String>();
   protected List<String> unwrappedExceptions = new ArrayList<String>();
   protected List<Class> actualResourceClasses = new ArrayList<Class>();
   protected List<ResourceFactory> resourceFactories = new ArrayList<ResourceFactory>();
   protected List<Object> resources = new ArrayList<Object>();
   protected Map<String, String> mediaTypeMappings = new HashMap<String, String>();
   protected Map<String, String> languageExtensions = new HashMap<String, String>();
   protected List<String> interceptorPrecedences = new ArrayList<String>();
   protected Map<String, String> interceptorBeforePrecedences = new HashMap<String, String>();
   protected Map<String, String> interceptorAfterPrecedences = new HashMap<String, String>();
   protected Registry registry;
   protected Dispatcher dispatcher;
   protected ResteasyProviderFactory providerFactory;
   protected String paramMapping;
   private final static Logger logger = LoggerFactory.getLogger(ResteasyDeployment.class);

   public void start()
   {
      // it is very important that each deployment create their own provider factory
      // this allows each WAR to have their own set of providers 
      if (providerFactory == null) providerFactory = new ResteasyProviderFactory();
      if (deploymentSensitiveFactoryEnabled)
      {
         // the ThreadLocalResteasyProviderFactory pushes and pops this deployments providerFactory
         // on a ThreadLocal stack.  This allows each application/WAR to have their own providerFactory
         // and still be able to call ResteasyProviderFactory.getInstance()
         if (!(providerFactory instanceof ThreadLocalResteasyProviderFactory))
         {
            ResteasyProviderFactory.setInstance(new ThreadLocalResteasyProviderFactory(providerFactory));
         }
      }
      else
      {
         ResteasyProviderFactory.setInstance(providerFactory);
      }
      if (asyncJobServiceEnabled)
      {
         AsynchronousDispatcher asyncDispatcher = new AsynchronousDispatcher(providerFactory);
         asyncDispatcher.setMaxCacheSize(asyncJobServiceMaxJobResults);
         asyncDispatcher.setMaxWaitMilliSeconds(asyncJobServiceMaxWait);
         asyncDispatcher.setThreadPoolSize(asyncJobServiceThreadPoolSize);
         asyncDispatcher.setBasePath(asyncJobServiceBasePath);
         asyncDispatcher.getUnwrappedExceptions().addAll(unwrappedExceptions);
         dispatcher = asyncDispatcher;
         asyncDispatcher.start();
      }
      else
      {
         SynchronousDispatcher dis = new SynchronousDispatcher(providerFactory);
         dis.getUnwrappedExceptions().addAll(unwrappedExceptions);
         dispatcher = dis;
      }
      registry = dispatcher.getRegistry();

      // Interceptor preferences should come before provider registration or builtin.

      if (interceptorPrecedences != null)
      {
         for (String precedence : interceptorPrecedences)
         {
            providerFactory.appendInterceptorPrecedence(precedence.trim());
         }
      }

      if (interceptorBeforePrecedences != null)
      {
         for (Map.Entry<String, String> ext : interceptorBeforePrecedences.entrySet())
         {
            providerFactory.insertInterceptorPrecedenceBefore(ext.getKey().trim(), ext.getValue().trim());
         }
      }
      if (interceptorAfterPrecedences != null)
      {
         for (Map.Entry<String, String> ext : interceptorAfterPrecedences.entrySet())
         {
            providerFactory.insertInterceptorPrecedenceAfter(ext.getKey().trim(), ext.getValue().trim());
         }
      }


      if (securityEnabled)
      {
         providerFactory.getServerPreProcessInterceptorRegistry().register(SecurityInterceptor.class);
      }

      if (registerBuiltin)
      {
         RegisterBuiltin.register(providerFactory);
      }

      if (applicationClass != null)
      {
         Class<?> clazz = null;
         try
         {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(applicationClass);
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }

         ConstructorInjector constructorInjector = providerFactory.getInjectorFactory().createConstructor(clazz.getConstructors()[0]);
         PropertyInjector propertyInjector = providerFactory.getInjectorFactory().createPropertyInjector(clazz);

         application = (Application) constructorInjector.construct();
         propertyInjector.inject(application);

      }

      // register all providers
      registration();

      if (paramMapping != null)
      {
         dispatcher.addHttpPreprocessor(new AcceptParameterHttpPreprocessor(paramMapping));
      }

      if (mediaTypeMappings != null)
      {
         Map<String, MediaType> extMap = new HashMap<String, MediaType>();
         for (Map.Entry<String, String> ext : mediaTypeMappings.entrySet())
         {
            String value = ext.getValue();
            extMap.put(ext.getKey().trim(), MediaType.valueOf(value.trim()));
         }
         if (dispatcher.getMediaTypeMappings() != null) dispatcher.getMediaTypeMappings().putAll(extMap);
         else dispatcher.setMediaTypeMappings(extMap);
      }


      if (languageExtensions != null)
      {
         if (dispatcher.getLanguageMappings() != null) dispatcher.getLanguageMappings().putAll(languageExtensions);
         else dispatcher.setLanguageMappings(languageExtensions);
      }
   }

   public void registration()
   {
      if (application != null) processApplication(application);

      if (providerClasses != null)
      {
         for (String provider : providerClasses)
         {
            registerProvider(provider);
         }
      }
      if (providers != null)
      {
         for (Object provider : providers)
         {
            providerFactory.registerProviderInstance(provider);
         }
      }

      for (Class actualProviderClass : actualProviderClasses)
      {
         providerFactory.registerProvider(actualProviderClass);
      }

      // All providers should be registered before resources because of interceptors.
      // interceptors must exist as they are applied only once when the resource is registered.

      if (jndiResources != null)
      {
         for (String resource : jndiResources)
         {
            registry.addJndiResource(resource.trim());
         }
      }
      if (resourceClasses != null)
      {
         for (String resource : resourceClasses)
         {
            Class clazz = null;
            try
            {
               clazz = Thread.currentThread().getContextClassLoader().loadClass(resource.trim());
            }
            catch (ClassNotFoundException e)
            {
               throw new RuntimeException(e);
            }
            registry.addPerRequestResource(clazz);
         }
      }

      if (resources != null)
      {
         for (Object obj : resources)
         {
            registry.addSingletonResource(obj);
         }
      }

      for (Class actualResourceClass : actualResourceClasses)
      {
         registry.addPerRequestResource(actualResourceClass);
      }

      for (ResourceFactory factory : resourceFactories)
      {
         registry.addResourceFactory(factory);
      }
   }

   public void stop()
   {

   }

   protected void processApplication(Application config)
   {
      logger.info("Deploying " + Application.class.getName() + ": " + config.getClass());
      if (config.getClasses() != null)
      {
         for (Class clazz : config.getClasses())
         {
            if (GetRestful.isRootResource(clazz))
            {
               actualResourceClasses.add(clazz);
            }
            else if (clazz.isAnnotationPresent(Provider.class))
            {
               actualProviderClasses.add(clazz);
            }
            else
            {
               logger.warn("Application.getClasses() returned unknown class type: " + clazz.getName());
            }
         }
      }
      if (config.getSingletons() != null)
      {
         for (Object obj : config.getSingletons())
         {
            if (GetRestful.isRootResource(obj.getClass()))
            {
               logger.info("Adding singleton resource " + obj.getClass().getName() + " from Application " + Application.class.getName());
               resources.add(obj);
            }
            else if (obj.getClass().isAnnotationPresent(Provider.class))
            {
               providers.add(obj);
            }
            else
            {
               logger.warn("Application.getSingletons() returned unknown class type: " + obj.getClass().getName());
            }
         }
      }
   }

   protected void registerProvider(String clazz)
   {
      Class provider = null;
      try
      {
         provider = Thread.currentThread().getContextClassLoader().loadClass(clazz.trim());
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
      providerFactory.registerProvider(provider);
   }

   public String getApplicationClass()
   {
      return applicationClass;
   }

   public void setApplicationClass(String applicationClass)
   {
      this.applicationClass = applicationClass;
   }

   public boolean isDeploymentSensitiveFactoryEnabled()
   {
      return deploymentSensitiveFactoryEnabled;
   }

   public void setDeploymentSensitiveFactoryEnabled(boolean deploymentSensitiveFactoryEnabled)
   {
      this.deploymentSensitiveFactoryEnabled = deploymentSensitiveFactoryEnabled;
   }

   public boolean isAsyncJobServiceEnabled()
   {
      return asyncJobServiceEnabled;
   }

   public void setAsyncJobServiceEnabled(boolean asyncJobServiceEnabled)
   {
      this.asyncJobServiceEnabled = asyncJobServiceEnabled;
   }

   public int getAsyncJobServiceMaxJobResults()
   {
      return asyncJobServiceMaxJobResults;
   }

   public void setAsyncJobServiceMaxJobResults(int asyncJobServiceMaxJobResults)
   {
      this.asyncJobServiceMaxJobResults = asyncJobServiceMaxJobResults;
   }

   public long getAsyncJobServiceMaxWait()
   {
      return asyncJobServiceMaxWait;
   }

   public void setAsyncJobServiceMaxWait(long asyncJobServiceMaxWait)
   {
      this.asyncJobServiceMaxWait = asyncJobServiceMaxWait;
   }

   public int getAsyncJobServiceThreadPoolSize()
   {
      return asyncJobServiceThreadPoolSize;
   }

   public void setAsyncJobServiceThreadPoolSize(int asyncJobServiceThreadPoolSize)
   {
      this.asyncJobServiceThreadPoolSize = asyncJobServiceThreadPoolSize;
   }

   public String getAsyncJobServiceBasePath()
   {
      return asyncJobServiceBasePath;
   }

   public void setAsyncJobServiceBasePath(String asyncJobServiceBasePath)
   {
      this.asyncJobServiceBasePath = asyncJobServiceBasePath;
   }

   public Application getApplication()
   {
      return application;
   }

   public void setApplication(Application application)
   {
      this.application = application;
   }

   public boolean isRegisterBuiltin()
   {
      return registerBuiltin;
   }

   public void setRegisterBuiltin(boolean registerBuiltin)
   {
      this.registerBuiltin = registerBuiltin;
   }

   public List<String> getProviderClasses()
   {
      return providerClasses;
   }

   public void setProviderClasses(List<String> providerClasses)
   {
      this.providerClasses = providerClasses;
   }

   public List<Object> getProviders()
   {
      return providers;
   }

   public void setProviders(List<Object> providers)
   {
      this.providers = providers;
   }

   public List<Class> getActualProviderClasses()
   {
      return actualProviderClasses;
   }

   public void setActualProviderClasses(List<Class> actualProviderClasses)
   {
      this.actualProviderClasses = actualProviderClasses;
   }

   public List<Class> getActualResourceClasses()
   {
      return actualResourceClasses;
   }

   public void setActualResourceClasses(List<Class> actualResourceClasses)
   {
      this.actualResourceClasses = actualResourceClasses;
   }

   public boolean isSecurityEnabled()
   {
      return securityEnabled;
   }

   public void setSecurityEnabled(boolean securityEnabled)
   {
      this.securityEnabled = securityEnabled;
   }

   public List<String> getJndiResources()
   {
      return jndiResources;
   }

   public void setJndiResources(List<String> jndiResources)
   {
      this.jndiResources = jndiResources;
   }

   public List<String> getResourceClasses()
   {
      return resourceClasses;
   }

   public void setResourceClasses(List<String> resourceClasses)
   {
      this.resourceClasses = resourceClasses;
   }

   public Map<String, String> getMediaTypeMappings()
   {
      return mediaTypeMappings;
   }

   public void setMediaTypeMappings(Map<String, String> mediaTypeMappings)
   {
      this.mediaTypeMappings = mediaTypeMappings;
   }

   public List<Object> getResources()
   {
      return resources;
   }

   public void setResources(List<Object> resources)
   {
      this.resources = resources;
   }

   public Map<String, String> getLanguageExtensions()
   {
      return languageExtensions;
   }

   public void setLanguageExtensions(Map<String, String> languageExtensions)
   {
      this.languageExtensions = languageExtensions;
   }

   public List<String> getInterceptorPrecedences()
   {
      return interceptorPrecedences;
   }

   public void setInterceptorPrecedences(List<String> interceptorPrecedences)
   {
      this.interceptorPrecedences = interceptorPrecedences;
   }

   public Map<String, String> getInterceptorBeforePrecedences()
   {
      return interceptorBeforePrecedences;
   }

   public void setInterceptorBeforePrecedences(Map<String, String> interceptorBeforePrecedences)
   {
      this.interceptorBeforePrecedences = interceptorBeforePrecedences;
   }

   public Map<String, String> getInterceptorAfterPrecedences()
   {
      return interceptorAfterPrecedences;
   }

   public void setInterceptorAfterPrecedences(Map<String, String> interceptorAfterPrecedences)
   {
      this.interceptorAfterPrecedences = interceptorAfterPrecedences;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setMediaTypeParamMapping(String paramMapping)
   {
      this.paramMapping = paramMapping;
   }

   public List<ResourceFactory> getResourceFactories()
   {
      return resourceFactories;
   }

   public void setResourceFactories(List<ResourceFactory> resourceFactories)
   {
      this.resourceFactories = resourceFactories;
   }

   public List<String> getUnwrappedExceptions()
   {
      return unwrappedExceptions;
   }

   public void setUnwrappedExceptions(List<String> unwrappedExceptions)
   {
      this.unwrappedExceptions = unwrappedExceptions;
   }
}
