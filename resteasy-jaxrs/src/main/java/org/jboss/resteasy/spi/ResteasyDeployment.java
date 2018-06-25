package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.AcceptHeaderByFileSuffixFilter;
import org.jboss.resteasy.core.AcceptParameterHttpPreprocessor;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.ServerFormUrlEncodedProvider;
import org.jboss.resteasy.plugins.server.resourcefactory.JndiComponentResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.GetRestful;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

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
   protected boolean widerRequestMatching;
   protected boolean useContainerFormParams = false;
   protected boolean deploymentSensitiveFactoryEnabled = false;
   protected boolean asyncJobServiceEnabled = false;
   protected boolean addCharset = true;
   protected int asyncJobServiceMaxJobResults = 100;
   protected long asyncJobServiceMaxWait = 300000;
   protected int asyncJobServiceThreadPoolSize = 100;
   protected String asyncJobServiceBasePath = "/asynch/jobs";
   protected String applicationClass;
   protected String injectorFactoryClass;
   protected Application application;
   protected boolean registerBuiltin = true;
   protected List<String> scannedResourceClasses = new ArrayList<String>();
   protected List<String> scannedProviderClasses = new ArrayList<String>();
   protected List<String> scannedJndiComponentResources = new ArrayList<String>();
   protected List<String> jndiComponentResources = new ArrayList<String>();
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
   protected Map<Class, Object> defaultContextObjects = new HashMap<Class, Object>();
   protected Map<String, String> constructedDefaultContextObjects = new HashMap<String, String>();
   protected Registry registry;
   protected Dispatcher dispatcher;
   protected ResteasyProviderFactory providerFactory;
   protected ThreadLocalResteasyProviderFactory threadLocalProviderFactory;
   protected String paramMapping;

   public void start()
   {
      try
      {
         startInternal();
      }
      finally
      {
         ThreadLocalResteasyProviderFactory.pop();
      }
   }

   @SuppressWarnings(value = {"unchecked", "deprecation"})
   protected void startInternal()
   {
      // it is very important that each deployment create their own provider factory
      // this allows each WAR to have their own set of providers
      if (providerFactory == null) providerFactory = ResteasyProviderFactory.newInstance();
      providerFactory.setRegisterBuiltins(registerBuiltin);

      Object tracingText;
      Object thresholdText;

      tracingText = System.getProperty(ResteasyContextParameters.RESTEASY_TRACING_TYPE);
      thresholdText = System.getProperty(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD);
      Object context = getDefaultContextObjects().get(ResteasyConfiguration.class);

      if (tracingText != null) {
         providerFactory.getMutableProperties().put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, tracingText);
      } else {
         if (context != null) {
            tracingText = ((ResteasyConfiguration) context).getParameter(ResteasyContextParameters.RESTEASY_TRACING_TYPE);
            if (tracingText != null) {
               providerFactory.getMutableProperties().put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, tracingText);
            }
         }
      }

      if (thresholdText != null) {
         providerFactory.getMutableProperties().put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, thresholdText);
      } else {

         if (context != null) {
            thresholdText = ((ResteasyConfiguration) context).getInitParameter(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD);
            if (thresholdText != null) {
               providerFactory.getMutableProperties().put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, thresholdText);
            }
         }
      }

      if (deploymentSensitiveFactoryEnabled)
      {
         // the ThreadLocalResteasyProviderFactory pushes and pops this deployments parentProviderFactory
         // on a ThreadLocal stack.  This allows each application/WAR to have their own parentProviderFactory
         // and still be able to call ResteasyProviderFactory.getInstance()
         if (!(providerFactory instanceof ThreadLocalResteasyProviderFactory))
         {
            if (ResteasyProviderFactory.peekInstance() == null || !(ResteasyProviderFactory.peekInstance() instanceof ThreadLocalResteasyProviderFactory))
            {

               threadLocalProviderFactory = new ThreadLocalResteasyProviderFactory(providerFactory);
               ResteasyProviderFactory.setInstance(threadLocalProviderFactory);
            }
            else
            {
               ThreadLocalResteasyProviderFactory.push(providerFactory);
            }
         }
         else
         {
            ThreadLocalResteasyProviderFactory.push(providerFactory);
         }
      }
      else
      {
         ResteasyProviderFactory.setInstance(providerFactory);
      }


      if (asyncJobServiceEnabled)
      {
         AsynchronousDispatcher asyncDispatcher;
         if (dispatcher == null) {
            asyncDispatcher = new AsynchronousDispatcher(providerFactory);
            dispatcher = asyncDispatcher;
         } else {
            asyncDispatcher = (AsynchronousDispatcher) dispatcher;
         }
         asyncDispatcher.setMaxCacheSize(asyncJobServiceMaxJobResults);
         asyncDispatcher.setMaxWaitMilliSeconds(asyncJobServiceMaxWait);
         asyncDispatcher.setThreadPoolSize(asyncJobServiceThreadPoolSize);
         asyncDispatcher.setBasePath(asyncJobServiceBasePath);
         asyncDispatcher.getUnwrappedExceptions().addAll(unwrappedExceptions);
         asyncDispatcher.start();
      }
      else
      {
         SynchronousDispatcher dis;
         if (dispatcher == null) {
            dis = new SynchronousDispatcher(providerFactory);
            dispatcher = dis;
         } else {
            dis = (SynchronousDispatcher) dispatcher;
         }
         dis.getUnwrappedExceptions().addAll(unwrappedExceptions);
      }
      registry = dispatcher.getRegistry();
      if (widerRequestMatching)
      {
         ((ResourceMethodRegistry)registry).setWiderMatching(widerRequestMatching);
      }


      dispatcher.getDefaultContextObjects().putAll(defaultContextObjects);
      dispatcher.getDefaultContextObjects().put(Configurable.class, providerFactory);
      dispatcher.getDefaultContextObjects().put(Configuration.class, providerFactory);
      dispatcher.getDefaultContextObjects().put(Providers.class, providerFactory);
      dispatcher.getDefaultContextObjects().put(Registry.class, registry);
      dispatcher.getDefaultContextObjects().put(Dispatcher.class, dispatcher);
      dispatcher.getDefaultContextObjects().put(InternalDispatcher.class, InternalDispatcher.getInstance());
      dispatcher.getDefaultContextObjects().put(ResteasyDeployment.class, this);

      // push context data so we can inject it
      Map contextDataMap = ResteasyProviderFactory.getContextDataMap();
      contextDataMap.putAll(dispatcher.getDefaultContextObjects());

      try
      {
         if (injectorFactoryClass != null)
         {
            InjectorFactory injectorFactory;
            try
            {
               Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(injectorFactoryClass);
               injectorFactory = (InjectorFactory) clazz.newInstance();
            }
            catch (ClassNotFoundException cnfe)
            {
               throw new RuntimeException(Messages.MESSAGES.unableToFindInjectorFactory(), cnfe);
            }
            catch (Exception e)
            {
               throw new RuntimeException(Messages.MESSAGES.unableToInstantiateInjectorFactory(), e);
            }

            providerFactory.setInjectorFactory(injectorFactory);
         }

         // feed context data map with constructed objects
         // see ResteasyContextParameters.RESTEASY_CONTEXT_OBJECTS
         if (constructedDefaultContextObjects != null && constructedDefaultContextObjects.size() > 0)
         {
            for (Map.Entry<String, String> entry : constructedDefaultContextObjects.entrySet())
            {
               Class<?> key = null;
               try
               {
                  key = Thread.currentThread().getContextClassLoader().loadClass(entry.getKey());
               }
               catch (ClassNotFoundException e)
               {
                  throw new RuntimeException(Messages.MESSAGES.unableToInstantiateContextObject(entry.getKey()), e);
               }
               Object obj = createFromInjectorFactory(entry.getValue(), providerFactory);
               LogMessages.LOGGER.creatingContextObject(entry.getKey(), entry.getValue());
               defaultContextObjects.put(key, obj);
               dispatcher.getDefaultContextObjects().put(key, obj);
               contextDataMap.put(key, obj);

            }
         }

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
            providerFactory.register(RoleBasedSecurityFeature.class);
         }


         if (registerBuiltin)
         {
            providerFactory.setRegisterBuiltins(true);
            RegisterBuiltin.register(providerFactory);

            // having problems using form parameters from container for a couple of TCK tests.  I couldn't figure out
            // why, specifically:
            // com/sun/ts/tests/jaxrs/spec/provider/standardhaspriority/JAXRSClient.java#readWriteMapProviderTest_from_standalone                                               Failed. Test case throws exception: [JAXRSCommonClient] null failed!  Check output for cause of failure.
            // com/sun/ts/tests/jaxrs/spec/provider/standardwithjaxrsclient/JAXRSClient.java#mapElementProviderTest_from_standalone                                             Failed. Test case throws exception: returned MultivaluedMap is null
            providerFactory.registerProviderInstance(new ServerFormUrlEncodedProvider(useContainerFormParams), null, null, true);
         }
         else
         {
            providerFactory.setRegisterBuiltins(false);
         }

         if (applicationClass != null)
         {
            application = createApplication(applicationClass, dispatcher, providerFactory);

         }

         // register all providers
         registration();

         if (paramMapping != null)
         {
            providerFactory.getContainerRequestFilterRegistry().registerSingleton(new AcceptParameterHttpPreprocessor(paramMapping));
         }

         AcceptHeaderByFileSuffixFilter suffixNegotiationFilter = null;
         if (mediaTypeMappings != null)
         {
            Map<String, MediaType> extMap = new HashMap<String, MediaType>();
            for (Map.Entry<String, String> ext : mediaTypeMappings.entrySet())
            {
               String value = ext.getValue();
               extMap.put(ext.getKey().trim(), MediaType.valueOf(value.trim()));
            }

            if (suffixNegotiationFilter == null)
            {
               suffixNegotiationFilter = new AcceptHeaderByFileSuffixFilter();
               providerFactory.getContainerRequestFilterRegistry().registerSingleton(suffixNegotiationFilter);
            }
            suffixNegotiationFilter.setMediaTypeMappings(extMap);
         }


         if (languageExtensions != null)
         {
            if (suffixNegotiationFilter == null)
            {
               suffixNegotiationFilter = new AcceptHeaderByFileSuffixFilter();
               providerFactory.getContainerRequestFilterRegistry().registerSingleton(suffixNegotiationFilter);
            }
            suffixNegotiationFilter.setLanguageMappings(languageExtensions);
         }
      }
      finally
      {
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }

   public void merge(ResteasyDeployment other)
   {
      scannedResourceClasses.addAll(other.getScannedResourceClasses());
      scannedProviderClasses.addAll(other.getScannedProviderClasses());
      scannedJndiComponentResources.addAll(other.getScannedJndiComponentResources());

      jndiComponentResources.addAll(other.getJndiComponentResources());
      providerClasses.addAll(other.getProviderClasses());
      actualProviderClasses.addAll(other.getActualProviderClasses());
      providers.addAll(other.getProviders());

      jndiResources.addAll(other.getJndiResources());
      resourceClasses.addAll(other.getResourceClasses());
      unwrappedExceptions.addAll(other.getUnwrappedExceptions());
      actualResourceClasses.addAll(other.getActualResourceClasses());
      resourceFactories.addAll(other.getResourceFactories());
      resources.addAll(other.getResources());

      mediaTypeMappings.putAll(other.getMediaTypeMappings());
      languageExtensions.putAll(other.getLanguageExtensions());
      interceptorPrecedences.addAll(other.getInterceptorPrecedences());
      interceptorBeforePrecedences.putAll(other.getInterceptorBeforePrecedences());
      interceptorAfterPrecedences.putAll(other.getInterceptorAfterPrecedences());

      defaultContextObjects.putAll(other.getDefaultContextObjects());
      constructedDefaultContextObjects.putAll(other.getConstructedDefaultContextObjects());
   }

   public static Application createApplication(String applicationClass, Dispatcher dispatcher, ResteasyProviderFactory providerFactory)
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

      Application app = (Application)providerFactory.createProviderInstance(clazz);
      dispatcher.getDefaultContextObjects().put(Application.class, app);
      ResteasyProviderFactory.pushContext(Application.class, app);
      PropertyInjector propertyInjector = providerFactory.getInjectorFactory().createPropertyInjector(clazz, providerFactory);
      propertyInjector.inject(app, false);
      return app;
   }

   public static Object createFromInjectorFactory(String classname, ResteasyProviderFactory providerFactory)
   {
      Class<?> clazz = null;
      try
      {
         clazz = Thread.currentThread().getContextClassLoader().loadClass(classname);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }

      Object obj = providerFactory.injectedInstance(clazz);

      return obj;
   }

   public void registration()
   {
      boolean useScanning = true;
      if (application != null)
      {
         dispatcher.getDefaultContextObjects().put(Application.class, application);
         ResteasyProviderFactory.getContextDataMap().put(Application.class, application);
         if (processApplication(application))
         {
            // Application class registered something so don't use scanning data.  See JAX-RS spec for more detail.
            useScanning = false;
         }
      }

      if (useScanning && scannedProviderClasses != null)
      {
         for (String provider : scannedProviderClasses)
         {
            registerProvider(provider);
         }
      }

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

      if (useScanning && scannedJndiComponentResources != null)
      {
         for (String resource : scannedJndiComponentResources)
         {
            registerJndiComponentResource(resource);
         }
      }
      if (jndiComponentResources != null)
      {
         for (String resource : jndiComponentResources)
         {
            registerJndiComponentResource(resource);
         }
      }
      if (jndiResources != null)
      {
         for (String resource : jndiResources)
         {
            registry.addJndiResource(resource.trim());
         }
      }

      if (useScanning && scannedResourceClasses != null)
      {
         for (String resource : scannedResourceClasses)
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
      registry.checkAmbiguousUri();
   }

   protected void registerJndiComponentResource(String resource)
   {
      String[] config = resource.trim().split(";");
      if (config.length < 3)
      {
         throw new RuntimeException(Messages.MESSAGES.jndiComponentResourceNotSetCorrectly());
      }
      String jndiName = config[0];
      Class clazz = null;
      try
      {
         clazz = Thread.currentThread().getContextClassLoader().loadClass(config[1]);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(Messages.MESSAGES.couldNotFindClassJndi(config[1]), e);
      }
      boolean cacheRefrence = Boolean.valueOf(config[2].trim());
      JndiComponentResourceFactory factory = new JndiComponentResourceFactory(jndiName, clazz, cacheRefrence);
      getResourceFactories().add(factory);

   }

   public void stop()
   {
      if (asyncJobServiceEnabled)
      {
         ((AsynchronousDispatcher) dispatcher).stop();
      }

      ResteasyProviderFactory.clearInstanceIfEqual(threadLocalProviderFactory);
      ResteasyProviderFactory.clearInstanceIfEqual(providerFactory);
   }

   /**
    * @param config application
    * @return whether application class registered anything. i.e. whether scanning metadata should be used or not
    */
   protected boolean processApplication(Application config)
   {
      LogMessages.LOGGER.deployingApplication(Application.class.getName(), config.getClass());
      boolean registered = false;
      if (config.getClasses() != null)
      {
         for (Class clazz : config.getClasses())
         {
            if (GetRestful.isRootResource(clazz))
            {
               LogMessages.LOGGER.addingClassResource(clazz.getName(), config.getClass());
               actualResourceClasses.add(clazz);
               registered = true;
            }
            else
            {
               LogMessages.LOGGER.addingProviderClass(clazz.getName(), config.getClass());
               actualProviderClasses.add(clazz);
               registered = true;
            }
         }
      }
      if (config.getSingletons() != null)
      {
         for (Object obj : config.getSingletons())
         {
            if (GetRestful.isRootResource(obj.getClass()))
            {
               if (actualResourceClasses.contains(obj.getClass()))
               {
                  LogMessages.LOGGER.singletonClassAlreadyDeployed("resource", obj.getClass().getName());
               }
               else
               {
                  LogMessages.LOGGER.addingSingletonResource(obj.getClass().getName(), config.getClass());
                  resources.add(obj);
                  registered = true;
               }
            }
            else
            {
               if (actualProviderClasses.contains(obj.getClass()))
               {
                  LogMessages.LOGGER.singletonClassAlreadyDeployed("provider", obj.getClass().getName());
               }
               else
               {
                  LogMessages.LOGGER.addingProviderSingleton(obj.getClass().getName(), config.getClass());
                  providers.add(obj);
                  registered = true;
               }
            }
         }
      }
      final Map<String, Object> properties = config.getProperties();
      if (properties != null && !properties.isEmpty())
      {
          Feature appliationPropertiesRegistrationfeature = new Feature()
          {
			 @Override
			 public boolean configure(FeatureContext featureContext)
			 {
				for (Map.Entry<String, Object> property : properties.entrySet())
				{
				   featureContext = featureContext.property(property.getKey(), property.getValue());
				}
				return true;
			 }
          };
	      this.providers.add(0, appliationPropertiesRegistrationfeature);
      }
      return registered;
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

   public boolean isUseContainerFormParams()
   {
      return useContainerFormParams;
   }

   public void setUseContainerFormParams(boolean useContainerFormParams)
   {
      this.useContainerFormParams = useContainerFormParams;
   }

   public List<String> getJndiComponentResources()
   {
      return jndiComponentResources;
   }

   public void setJndiComponentResources(List<String> jndiComponentResources)
   {
      this.jndiComponentResources = jndiComponentResources;
   }

   public String getApplicationClass()
   {
      return applicationClass;
   }

   public void setApplicationClass(String applicationClass)
   {
      this.applicationClass = applicationClass;
   }

   public String getInjectorFactoryClass()
   {
      return injectorFactoryClass;
   }

   public void setInjectorFactoryClass(String injectorFactoryClass)
   {
      this.injectorFactoryClass = injectorFactoryClass;
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

   public Map<String, String> getConstructedDefaultContextObjects()
   {
      return constructedDefaultContextObjects;
   }

   public void setConstructedDefaultContextObjects(Map<String, String> constructedDefaultContextObjects)
   {
      this.constructedDefaultContextObjects = constructedDefaultContextObjects;
   }

   public Map<Class, Object> getDefaultContextObjects()
   {
      return defaultContextObjects;
   }

   public void setDefaultContextObjects(Map<Class, Object> defaultContextObjects)
   {
      this.defaultContextObjects = defaultContextObjects;
   }

   public List<String> getScannedResourceClasses()
   {
      return scannedResourceClasses;
   }

   public void setScannedResourceClasses(List<String> scannedResourceClasses)
   {
      this.scannedResourceClasses = scannedResourceClasses;
   }

   public List<String> getScannedProviderClasses()
   {
      return scannedProviderClasses;
   }

   public void setScannedProviderClasses(List<String> scannedProviderClasses)
   {
      this.scannedProviderClasses = scannedProviderClasses;
   }

   public List<String> getScannedJndiComponentResources()
   {
      return scannedJndiComponentResources;
   }

   public void setScannedJndiComponentResources(List<String> scannedJndiComponentResources)
   {
      this.scannedJndiComponentResources = scannedJndiComponentResources;
   }

   public boolean isWiderRequestMatching()
   {
      return widerRequestMatching;
   }

   public void setWiderRequestMatching(boolean widerRequestMatching)
   {
      this.widerRequestMatching = widerRequestMatching;
   }

   public boolean isAddCharset()
   {
      return addCharset;
   }

   public void setAddCharset(boolean addCharset)
   {
      this.addCharset = addCharset;
   }
}
