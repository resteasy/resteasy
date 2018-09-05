package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.core.interception.jaxrs.ClientRequestFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ContainerRequestFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.jaxrs.WriterInterceptorRegistry;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.DateDelegate;
import org.jboss.resteasy.plugins.delegates.EntityTagDelegate;
import org.jboss.resteasy.plugins.delegates.LinkDelegate;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.UriHeaderDelegate;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.LinkBuilderImpl;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.specimpl.VariantListBuilderImpl;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;

import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.FeatureContextDelegate;
import org.jboss.resteasy.util.PickConstructor;
import org.jboss.resteasy.util.ThreadLocalStack;
import org.jboss.resteasy.util.Types;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.WriterInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ResteasyProviderFactory extends RuntimeDelegate implements Providers, HeaderValueProcessor, Configurable<ResteasyProviderFactory>, Configuration
{

   /**
    * Allow us to sort message body implementations that are more specific for their types
    * i.e. MessageBodyWriter&#x3C;Object&#x3E; is less specific than MessageBodyWriter&#x3C;String&#x3E;.
    * <p>
    * This helps out a lot when the desired media type is a wildcard and to weed out all the possible
    * default mappings.
    */
   private static class SortedKey<T> implements Comparable<SortedKey<T>>, MediaTypeMap.Typed
   {
      private final T obj;
      private final boolean isBuiltin;
      private final Class<?> template;
      private final int priority;

      protected SortedKey(Class<?> intf, T reader, Class<?> readerClass, int priority, boolean isBuiltin)
      {
         this.obj = reader;
         // check the super class for the generic type 1st
         Class<?> t = Types.getTemplateParameterOfInterface(readerClass, intf);
         template = (t != null) ? t : Object.class;
         this.priority = priority;
         this.isBuiltin = isBuiltin;
      }

      protected SortedKey(Class<?> intf, T reader, Class<?> readerClass, boolean isBuiltin)
      {
         this(intf, reader, readerClass, Priorities.USER, isBuiltin);
      }

      protected SortedKey(Class<?> intf, T reader, Class<?> readerClass)
      {
         this(intf, reader, readerClass, Priorities.USER, false);
      }

      public int compareTo(SortedKey<T> tMessageBodyKey)
      {
         // Sort user provider before builtins
         if (this == tMessageBodyKey) return 0;
         if (isBuiltin == tMessageBodyKey.isBuiltin)
         {
            if (this.priority < tMessageBodyKey.priority)
            {
               return -1;
            }
            if (this.priority == tMessageBodyKey.priority)
            {
               return 0;
            }
            if (this.priority > tMessageBodyKey.priority)
            {
               return 1;
            }
         }
         if (isBuiltin) return 1;
         else return -1;
      }

      public Class<?> getType()
      {
         return template;
      }

       public T getObj() {
           return obj;
       }
   }

   private static class ExtSortedKey<T> extends SortedKey<T>
   {
      protected ExtSortedKey(Class<?> intf, T reader, Class<?> readerClass, int priority, boolean isBuiltin)
      {
         super(intf, reader, readerClass, priority, isBuiltin);
      }

      protected ExtSortedKey(Class<?> intf, T reader, Class<?> readerClass, boolean isBuiltin)
      {
         super(intf, reader, readerClass, isBuiltin);
      }

      protected ExtSortedKey(Class<?> intf, T reader, Class<?> readerClass)
      {
         super(intf, reader, readerClass);
      }

      @Override
      public int compareTo(SortedKey<T> tMessageBodyKey)
      {
         int c = super.compareTo(tMessageBodyKey);
         if (c != 0)
         {
            return c;
         }
         if (this.getObj() == tMessageBodyKey.getObj())
         {
            return 0;
         }
         return -1;
      }
   }

   private static final ThreadLocalStack<Map<Class<?>, Object>> contextualData = new ThreadLocalStack<Map<Class<?>, Object>>();
   private static final int maxForwards = 20;
   private static volatile ResteasyProviderFactory instance;
   private static boolean registerBuiltinByDefault = true;

   private MediaTypeMap<SortedKey<MessageBodyReader>> serverMessageBodyReaders;
   private MediaTypeMap<SortedKey<MessageBodyWriter>> serverMessageBodyWriters;
   private MediaTypeMap<SortedKey<MessageBodyReader>> clientMessageBodyReaders;
   private MediaTypeMap<SortedKey<MessageBodyWriter>> clientMessageBodyWriters;
   private Map<Class<?>, SortedKey<ExceptionMapper>> sortedExceptionMappers;
   private Map<Class<?>, ExceptionMapper> exceptionMappers;
   private Map<Class<?>, AsyncResponseProvider> asyncResponseProviders;
   private Map<Class<?>, AsyncClientResponseProvider> asyncClientResponseProviders;
   private Map<Class<?>, AsyncStreamProvider> asyncStreamProviders;
   private Map<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> contextResolvers;
   private Map<Type, ContextInjector> contextInjectors;
   private Map<Type, ContextInjector> asyncContextInjectors;
   private Set<ExtSortedKey<ParamConverterProvider>> sortedParamConverterProviders;
   private Map<Class<?>, Class<? extends StringParameterUnmarshaller>> stringParameterUnmarshallers;
   protected Map<Class<?>, Map<Class<?>, Integer>> classContracts;

   private Map<Class<?>, HeaderDelegate> headerDelegates;

   private ReaderInterceptorRegistry serverReaderInterceptorRegistry;
   private WriterInterceptorRegistry serverWriterInterceptorRegistry;
   private ContainerRequestFilterRegistry containerRequestFilterRegistry;
   private ContainerResponseFilterRegistry containerResponseFilterRegistry;

   private ClientRequestFilterRegistry clientRequestFilterRegistry;

   private ClientResponseFilterRegistry clientResponseFilters;
   private ReaderInterceptorRegistry clientReaderInterceptorRegistry;
   private WriterInterceptorRegistry clientWriterInterceptorRegistry;

   private boolean builtinsRegistered = false;
   private boolean registerBuiltins = true;

   private InjectorFactory injectorFactory;
   private ResteasyProviderFactory parent;

   private Set<DynamicFeature> serverDynamicFeatures;
   private Set<DynamicFeature> clientDynamicFeatures;
   private Map<String, Object> properties;
   private Map<Class<?>, Class<? extends RxInvokerProvider<?>>> reactiveClasses;

   private ResourceBuilder resourceBuilder;

   protected Set<Feature> enabledFeatures;
   protected Set<Class<?>> providerClasses;
   protected Set<Object> providerInstances;

   public ResteasyProviderFactory()
   {
      // NOTE!!! It is important to put all initialization into initialize() as ThreadLocalResteasyProviderFactory
      // subclasses and delegates to this class.
      initialize();
   }

   /**
    * Copies a specific component registry when a new
    * provider is added. Otherwise delegates to the parent.
    *
    * @param parent provider factory
    */
   public ResteasyProviderFactory(ResteasyProviderFactory parent)
   {
      this(parent, false);
   }

   /**
    * If local is true, copies components needed by client configuration,
    * so that parent is not referenced.
    * @param parent provider factory
    * @param local local
    */
   public ResteasyProviderFactory(ResteasyProviderFactory parent, boolean local)
   {
      if (local || parent == null)
      {
         // Parent MUST not be referenced after current object is created
         this.parent = null;
         initialize(parent);
      }
      else
      {
         this.parent = parent;
         providerClasses = new CopyOnWriteArraySet<>();
         providerInstances = new CopyOnWriteArraySet<>();
         properties = new ConcurrentHashMap<>();
         properties.putAll(parent.getProperties());
         enabledFeatures = new CopyOnWriteArraySet<>();
         reactiveClasses = new ConcurrentHashMap<>();
         resourceBuilder = new ResourceBuilder();
      }
   }

   protected void initialize()
   {
      initialize(null);
   }
   
   protected void initialize(ResteasyProviderFactory parent)
   {
      serverDynamicFeatures = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getServerDynamicFeatures());
      clientDynamicFeatures = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getClientDynamicFeatures());
      enabledFeatures = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getEnabledFeatures());
      properties = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getProperties());
      providerClasses = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getProviderClasses());
      providerInstances = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getProviderInstances());
      classContracts = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getClassContracts());
      serverMessageBodyReaders = parent == null ? new MediaTypeMap<>() : parent.getServerMessageBodyReaders().clone();
      serverMessageBodyWriters = parent == null ? new MediaTypeMap<>() : parent.getServerMessageBodyWriters().clone();
      clientMessageBodyReaders = parent == null ? new MediaTypeMap<>() : parent.getClientMessageBodyReaders().clone();
      clientMessageBodyWriters = parent == null ? new MediaTypeMap<>() : parent.getClientMessageBodyWriters().clone();
      sortedExceptionMappers = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getSortedExceptionMappers());
      asyncResponseProviders = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getAsyncResponseProviders());
      asyncClientResponseProviders = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getAsyncClientResponseProviders());
      asyncStreamProviders = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getAsyncStreamProviders());
      contextResolvers = new ConcurrentHashMap<>();
      if (parent != null)
      {
         for (Map.Entry<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> entry : parent.getContextResolvers()
               .entrySet())
         {
            contextResolvers.put(entry.getKey(), entry.getValue().clone());
         }
      }
      contextInjectors = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getContextInjectors());
      asyncContextInjectors = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getAsyncContextInjectors());
      sortedParamConverterProviders = Collections.synchronizedSortedSet(parent == null ? new TreeSet<>() : new TreeSet<>(parent.getSortedParamConverterProviders()));
      stringParameterUnmarshallers = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getStringParameterUnmarshallers());
      reactiveClasses = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.reactiveClasses);
      headerDelegates = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getHeaderDelegates());
      addHeaderDelegateIfAbsent(MediaType.class, new MediaTypeHeaderDelegate());
      addHeaderDelegateIfAbsent(NewCookie.class, new NewCookieHeaderDelegate());
      addHeaderDelegateIfAbsent(Cookie.class, new CookieHeaderDelegate());
      addHeaderDelegateIfAbsent(URI.class, new UriHeaderDelegate());
      addHeaderDelegateIfAbsent(EntityTag.class, new EntityTagDelegate());
      addHeaderDelegateIfAbsent(CacheControl.class, new CacheControlDelegate());
      addHeaderDelegateIfAbsent(Locale.class, new LocaleDelegate());
      addHeaderDelegateIfAbsent(LinkHeader.class, new LinkHeaderDelegate());
      addHeaderDelegateIfAbsent(javax.ws.rs.core.Link.class, new LinkDelegate());
      addHeaderDelegateIfAbsent(Date.class, new DateDelegate());

      resourceBuilder = new ResourceBuilder();

      initializeRegistriesAndFilters(parent);

      builtinsRegistered = false;
      registerBuiltins = true;

      injectorFactory = parent == null ? new InjectorFactoryImpl() : parent.getInjectorFactory();
   }

   private void initializeRegistriesAndFilters(ResteasyProviderFactory parent)
   {
      serverReaderInterceptorRegistry = parent == null ? new ReaderInterceptorRegistry(this) : parent.getServerReaderInterceptorRegistry().clone(this);
      serverWriterInterceptorRegistry = parent == null ? new WriterInterceptorRegistry(this) : parent.getServerWriterInterceptorRegistry().clone(this);
      containerRequestFilterRegistry = parent == null ? new ContainerRequestFilterRegistry(this): parent.getContainerRequestFilterRegistry().clone(this);
      containerResponseFilterRegistry = parent == null ? new ContainerResponseFilterRegistry(this) : parent.getContainerResponseFilterRegistry().clone(this);

      clientRequestFilterRegistry = parent == null ? new ClientRequestFilterRegistry(this) : parent.getClientRequestFilterRegistry().clone(this);
      clientResponseFilters = parent == null ? new ClientResponseFilterRegistry(this) : parent.getClientResponseFilters().clone(this);
      clientReaderInterceptorRegistry = parent == null ? new ReaderInterceptorRegistry(this) : parent.getClientReaderInterceptorRegistry().clone(this);
      clientWriterInterceptorRegistry = parent == null ? new WriterInterceptorRegistry(this) : parent.getClientWriterInterceptorRegistry().clone(this);
   }

   public Set<DynamicFeature> getServerDynamicFeatures()
   {
      if (serverDynamicFeatures == null && parent != null) return parent.getServerDynamicFeatures();
      return serverDynamicFeatures;
   }

   public Set<DynamicFeature> getClientDynamicFeatures()
   {
      if (clientDynamicFeatures == null && parent != null) return parent.getClientDynamicFeatures();
      return clientDynamicFeatures;
   }


   private MediaTypeMap<SortedKey<MessageBodyReader>> getServerMessageBodyReaders()
   {
      if (serverMessageBodyReaders == null && parent != null) return parent.getServerMessageBodyReaders();
      return serverMessageBodyReaders;
   }

   private MediaTypeMap<SortedKey<MessageBodyWriter>> getServerMessageBodyWriters()
   {
      if (serverMessageBodyWriters == null && parent != null) return parent.getServerMessageBodyWriters();
      return serverMessageBodyWriters;
   }

   private MediaTypeMap<SortedKey<MessageBodyReader>> getClientMessageBodyReaders()
   {
      if (clientMessageBodyReaders == null && parent != null) return parent.getClientMessageBodyReaders();
      return clientMessageBodyReaders;
   }

   private MediaTypeMap<SortedKey<MessageBodyWriter>> getClientMessageBodyWriters()
   {
      if (clientMessageBodyWriters == null && parent != null) return parent.getClientMessageBodyWriters();
      return clientMessageBodyWriters;
   }



   public Map<Class<?>, ExceptionMapper> getExceptionMappers()
   {
      if (exceptionMappers != null)
      {
         return exceptionMappers;
      }
      Map<Class<?>, ExceptionMapper> map = new ConcurrentHashMap<Class<?>, ExceptionMapper>();
      for (Entry<Class<?>, SortedKey<ExceptionMapper>> entry : getSortedExceptionMappers().entrySet())
      {
         map.put(entry.getKey(), entry.getValue().getObj());
      }
      exceptionMappers = map;
      return map;
   }

   private Map<Class<?>, SortedKey<ExceptionMapper>> getSortedExceptionMappers()
   {
      if (sortedExceptionMappers == null && parent != null) return parent.getSortedExceptionMappers();
      return sortedExceptionMappers;
   }

   public Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders()
   {
      if (asyncResponseProviders == null && parent != null) return parent.getAsyncResponseProviders();
      return asyncResponseProviders;
   }

   public Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders()
   {
      if (asyncClientResponseProviders == null && parent != null) return parent.getAsyncClientResponseProviders();
      return asyncClientResponseProviders;
   }
   
   public Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders()
   {
      if (asyncStreamProviders == null && parent != null) return parent.getAsyncStreamProviders();
      return asyncStreamProviders;
   }

   public Map<Type, ContextInjector> getContextInjectors()
   {
      if (contextInjectors == null && parent != null) return parent.getContextInjectors();
      return contextInjectors;
   }

   public Map<Type, ContextInjector> getAsyncContextInjectors()
   {
      if (asyncContextInjectors == null && parent != null) return parent.getAsyncContextInjectors();
      return asyncContextInjectors;
   }

   private Map<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> getContextResolvers()
   {
      if (contextResolvers == null && parent != null) return parent.getContextResolvers();
      return contextResolvers;
   }

   private Set<ExtSortedKey<ParamConverterProvider>> getSortedParamConverterProviders()
   {
      if (sortedParamConverterProviders == null && parent != null) return parent.getSortedParamConverterProviders();
      return sortedParamConverterProviders;
   }

   private Map<Class<?>, Class<? extends StringParameterUnmarshaller>> getStringParameterUnmarshallers()
   {
      if (stringParameterUnmarshallers == null && parent != null) return parent.getStringParameterUnmarshallers();
      return stringParameterUnmarshallers;
   }

   /**
    * Gets provide classes.
    *
    * @return set of provider classes
    */
   public Set<Class<?>> getProviderClasses()
   {
      if (providerClasses == null && parent != null) return parent.getProviderClasses();
      Set<Class<?>> set = new HashSet<Class<?>>();
      if (parent != null) set.addAll(parent.getProviderClasses());
      set.addAll(providerClasses);
      return set;
   }

   /**
    * Gets provider instances.
    *
    * @return set of provider instances
    */
   public Set<Object> getProviderInstances()
   {
      if (providerInstances == null && parent != null) return parent.getProviderInstances();
      Set<Object> set = new HashSet<Object>();
      if (parent != null) set.addAll(parent.getProviderInstances());
      set.addAll(providerInstances);
      return set;
   }

   private Map<Class<?>, Map<Class<?>, Integer>> getClassContracts()
   {
      if (classContracts != null) return classContracts;
      Map<Class<?>, Map<Class<?>, Integer>> map = new ConcurrentHashMap<Class<?>, Map<Class<?>, Integer>>();
      if (parent != null)
      {
         for (Map.Entry<Class<?>, Map<Class<?>, Integer>> entry : parent.getClassContracts().entrySet())
         {
            Map<Class<?>, Integer> mapEntry = new HashMap<Class<?>, Integer>();
            mapEntry.putAll(entry.getValue());
            map.put(entry.getKey(), mapEntry);
         }
      }
      classContracts = map;
      return classContracts;
   }

   public static <T> void pushContext(Class<T> type, T data)
   {
      getContextDataMap().put(type, data);
   }

   public static void pushContextDataMap(Map<Class<?>, Object> map)
   {
      contextualData.push(map);
   }

   public static Map<Class<?>, Object> getContextDataMap()
   {
      return getContextDataMap(true);
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) getContextDataMap().get(type);
   }

   public boolean hasAsyncContextData(Type genericType) {
      return getAsyncContextInjectors().containsKey(Types.boxPrimitives(genericType));
   }
   
   public <T> T getContextData(Class<T> rawType, Type genericType, Annotation[] annotations, boolean unwrapAsync)
   {
      T ret = (T) getContextDataMap().get(rawType);
      if(ret != null)
         return ret;
      ContextInjector contextInjector = getContextInjectors().get(genericType);
      boolean async = false;
      if(contextInjector == null && unwrapAsync)
      {
         contextInjector = getAsyncContextInjectors().get(Types.boxPrimitives(genericType));
         async = true;
      }
      
      if(contextInjector != null)
      {
         ret = (T) contextInjector.resolve(rawType, genericType, annotations);
         if(async && ret != null)
         {
            Type wrappedType = Types.getActualTypeArgumentsOfAnInterface(contextInjector.getClass(), ContextInjector.class)[0];
            Class<?> rawWrappedType = Types.getRawType(wrappedType);
            AsyncResponseProvider converter = getAsyncResponseProvider(rawWrappedType);
            // OK this is plain lying
            ret = (T) converter.toCompletionStage(ret);
         }
      }
      return ret;
   }

   public static <T> T popContextData(Class<T> type)
   {
      return (T) getContextDataMap().remove(type);
   }

   public static void clearContextData()
   {
      contextualData.clear();
   }

   private static Map<Class<?>, Object> getContextDataMap(boolean create)
   {
      Map<Class<?>, Object> map = contextualData.get();
      if (map == null)
      {
         contextualData.setLast(map = new HashMap<Class<?>, Object>());
      }
      return map;
   }

   public static Map<Class<?>, Object> addContextDataLevel()
   {
      if (getContextDataLevelCount() == maxForwards)
      {
         throw new BadRequestException(Messages.MESSAGES.excededMaximumForwards(getContextData(UriInfo.class).getPath()));
      }
      Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
      contextualData.push(map);
      return map;
   }

   public static int getContextDataLevelCount()
   {
      return contextualData.size();
   }

   public static void removeContextDataLevel()
   {
      contextualData.pop();
   }

   /**
    * Will not initialize singleton if not set.
    *
    * @return provider factory singleton
    */
   public static ResteasyProviderFactory peekInstance()
   {
      return instance;
   }

   public synchronized static void clearInstanceIfEqual(ResteasyProviderFactory factory)
   {
      if (instance == factory)
      {
         instance = null;
         RuntimeDelegate.setInstance(null);
      }
   }

   public synchronized static void setInstance(ResteasyProviderFactory factory)
   {
      synchronized (RD_LOCK)
      {
         instance = factory;
      }
      RuntimeDelegate.setInstance(factory);
   }

   final static Object RD_LOCK = new Object();

   /**
    * Initializes ResteasyProviderFactory singleton if not set.
    *
    * @return singleton provider factory
    */
   public static ResteasyProviderFactory getInstance()
   {
      ResteasyProviderFactory result = instance;
      if (result == null)
      { // First check (no locking)
         synchronized (RD_LOCK)
         {
            result = instance;
            if (result == null)
            { // Second check (with locking)
               RuntimeDelegate runtimeDelegate = RuntimeDelegate.getInstance();
               if (runtimeDelegate instanceof ResteasyProviderFactory)
               {
                  instance = result = (ResteasyProviderFactory) runtimeDelegate;
               }
               else
               {
                  instance = result = new ResteasyProviderFactory();
               }
               if (registerBuiltinByDefault) RegisterBuiltin.register(instance);
            }
         }
      }
      return instance;
   }

   public static ResteasyProviderFactory newInstance()
   {
      return new ResteasyProviderFactory();
   }

   public static void setRegisterBuiltinByDefault(boolean registerBuiltinByDefault)
   {
      ResteasyProviderFactory.registerBuiltinByDefault = registerBuiltinByDefault;
   }


   public boolean isRegisterBuiltins()
   {
      return registerBuiltins;
   }

   public void setRegisterBuiltins(boolean registerBuiltins)
   {
      this.registerBuiltins = registerBuiltins;
   }

   public InjectorFactory getInjectorFactory()
   {
      if (injectorFactory == null && parent != null) return parent.getInjectorFactory();
      return injectorFactory;
   }

   public void setInjectorFactory(InjectorFactory injectorFactory)
   {
      this.injectorFactory = injectorFactory;
   }

   public ReaderInterceptorRegistry getServerReaderInterceptorRegistry()
   {
      if (serverReaderInterceptorRegistry == null && parent != null) return parent.getServerReaderInterceptorRegistry();
      return serverReaderInterceptorRegistry;
   }

   public WriterInterceptorRegistry getServerWriterInterceptorRegistry()
   {
      if (serverWriterInterceptorRegistry == null && parent != null) return parent.getServerWriterInterceptorRegistry();
      return serverWriterInterceptorRegistry;
   }

   public ContainerRequestFilterRegistry getContainerRequestFilterRegistry()
   {
      if (containerRequestFilterRegistry == null && parent != null) return parent.getContainerRequestFilterRegistry();
      return containerRequestFilterRegistry;
   }

   public ContainerResponseFilterRegistry getContainerResponseFilterRegistry()
   {
      if (containerResponseFilterRegistry == null && parent != null) return parent.getContainerResponseFilterRegistry();
      return containerResponseFilterRegistry;
   }

   public ReaderInterceptorRegistry getClientReaderInterceptorRegistry()
   {
      if (clientReaderInterceptorRegistry == null && parent != null) return parent.getClientReaderInterceptorRegistry();
      return clientReaderInterceptorRegistry;
   }

   public WriterInterceptorRegistry getClientWriterInterceptorRegistry()
   {
      if (clientWriterInterceptorRegistry == null && parent != null) return parent.getClientWriterInterceptorRegistry();
      return clientWriterInterceptorRegistry;
   }

   public ClientRequestFilterRegistry getClientRequestFilterRegistry()
   {
      if (clientRequestFilterRegistry == null && parent != null) return parent.getClientRequestFilterRegistry();
      return clientRequestFilterRegistry;
   }

   public ClientResponseFilterRegistry getClientResponseFilters()
   {
      if (clientResponseFilters == null && parent != null) return parent.getClientResponseFilters();
      return clientResponseFilters;
   }

   public boolean isBuiltinsRegistered()
   {
      return builtinsRegistered;
   }

   public void setBuiltinsRegistered(boolean builtinsRegistered)
   {
      this.builtinsRegistered = builtinsRegistered;
   }

   public UriBuilder createUriBuilder()
   {
      return new ResteasyUriBuilder();
   }

   public Response.ResponseBuilder createResponseBuilder()
   {
      return new ResponseBuilderImpl();
   }

   public Variant.VariantListBuilder createVariantListBuilder()
   {
      return new VariantListBuilderImpl();
   }

   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass)
   {
      if (tClass == null) throw new IllegalArgumentException(Messages.MESSAGES.tClassParameterNull());
      if (headerDelegates == null && parent != null) return parent.createHeaderDelegate(tClass);

      Class<?> clazz = tClass;
      while (clazz != null)
      {
         HeaderDelegate<T> delegate = headerDelegates.get(clazz);
         if (delegate != null)
         {
            return delegate;
         }
         delegate = createHeaderDelegateFromInterfaces(clazz.getInterfaces());
         if (delegate != null)
         {
            return delegate;
         }
         clazz = clazz.getSuperclass();
      }

      return createHeaderDelegateFromInterfaces(tClass.getInterfaces());
   }

   private <T> HeaderDelegate<T> createHeaderDelegateFromInterfaces(Class<?>[] interfaces)
   {
      HeaderDelegate<T> delegate = null;
      for (int i = 0; i < interfaces.length; i++)
      {
         delegate = headerDelegates.get(interfaces[i]);
         if (delegate != null)
         {
            return delegate;
         }
         delegate = createHeaderDelegateFromInterfaces(interfaces[i].getInterfaces());
         if (delegate != null)
         {
            return delegate;
         }
      }
      return null;
   }

   private Map<Class<?>, HeaderDelegate> getHeaderDelegates()
   {
      if (headerDelegates == null && parent != null) return parent.getHeaderDelegates();
      return headerDelegates;
   }
   
   private void addHeaderDelegateIfAbsent(Class clazz, HeaderDelegate header)
   {
      if (headerDelegates == null || !headerDelegates.containsKey(clazz))
      {
         addHeaderDelegate(clazz, header);
      }
   }

   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      if (headerDelegates == null)
      {
         headerDelegates = new ConcurrentHashMap<Class<?>, HeaderDelegate>();
         headerDelegates.putAll(parent.getHeaderDelegates());
      }
      headerDelegates.put(clazz, header);
   }

   /**
    * Specify the provider class.  This is there jsut in case the provider instance is a proxy.  Proxies tend
    * to lose generic type information.
    *
    * @param provider message reader
    * @param providerClass provider class
    * @param priority priority
    * @param isBuiltin built-in
    */

   private void addMessageBodyReader(MessageBodyReader provider, Class<?> providerClass, int priority, boolean isBuiltin)
   {
      SortedKey<MessageBodyReader> key = new SortedKey<MessageBodyReader>(MessageBodyReader.class, provider, providerClass, priority, isBuiltin);
      injectProperties(providerClass, provider);
      Consumes consumeMime = provider.getClass().getAnnotation(Consumes.class);
      RuntimeType type = null;
      ConstrainedTo constrainedTo = providerClass.getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null) type = constrainedTo.value();

      if ((type == null || type == RuntimeType.CLIENT) && clientMessageBodyReaders == null)
      {
         clientMessageBodyReaders = parent.getClientMessageBodyReaders().clone();
      }
      if ((type == null || type == RuntimeType.SERVER) && serverMessageBodyReaders == null)
      {
         serverMessageBodyReaders = parent.getServerMessageBodyReaders().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            if (type == null) {
               clientMessageBodyReaders.add(MediaType.valueOf(consume), key);
               serverMessageBodyReaders.add(MediaType.valueOf(consume), key);
            } else if (type == RuntimeType.CLIENT) {
               clientMessageBodyReaders.add(MediaType.valueOf(consume), key);
            } else {
               serverMessageBodyReaders.add(MediaType.valueOf(consume), key);
            }
         }
      }
      else
      {
         if (type == null) {
            clientMessageBodyReaders.add(new MediaType("*", "*"), key);
            serverMessageBodyReaders.add(new MediaType("*", "*"), key);
         } else if (type == RuntimeType.CLIENT) {
            clientMessageBodyReaders.add(new MediaType("*", "*"), key);
         } else {
            serverMessageBodyReaders.add(new MediaType("*", "*"), key);
         }
      }
   }

   /**
    * Specify the provider class.  This is there jsut in case the provider instance is a proxy.  Proxies tend
    * to lose generic type information
    *
    * @param provider message reader
    * @param providerClass provider class
    * @param priority priority
    * @param isBuiltin built-in
    */
   private void addMessageBodyWriter(MessageBodyWriter provider, Class<?> providerClass, int priority, boolean isBuiltin)
   {
      injectProperties(providerClass, provider);
      Produces consumeMime = provider.getClass().getAnnotation(Produces.class);
      SortedKey<MessageBodyWriter> key = new SortedKey<MessageBodyWriter>(MessageBodyWriter.class, provider, providerClass, priority, isBuiltin);
      RuntimeType type = null;
      ConstrainedTo constrainedTo = providerClass.getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null) type = constrainedTo.value();
      
      if ((type == null || type == RuntimeType.CLIENT) && clientMessageBodyWriters == null)
      {
         clientMessageBodyWriters = parent.getClientMessageBodyWriters().clone();
      }
      if ((type == null || type == RuntimeType.SERVER) && serverMessageBodyWriters == null)
      {
         serverMessageBodyWriters = parent.getServerMessageBodyWriters().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            //logger.info(">>> Adding provider: " + provider.getClass().getName() + " with mime type of: " + mime);
            if (type == null) {
               clientMessageBodyWriters.add(MediaType.valueOf(consume), key);
               serverMessageBodyWriters.add(MediaType.valueOf(consume), key);
            } else if (type == RuntimeType.CLIENT) {
               clientMessageBodyWriters.add(MediaType.valueOf(consume), key);
            } else {
               serverMessageBodyWriters.add(MediaType.valueOf(consume), key);   
            }
         }
      }
      else
      {
         //logger.info(">>> Adding provider: " + provider.getClass().getName() + " with mime type of: default */*");
         if (type == null) {
            clientMessageBodyWriters.add(new MediaType("*", "*"), key);
            serverMessageBodyWriters.add(new MediaType("*", "*"), key);
         } else if (type == RuntimeType.CLIENT) {
            clientMessageBodyWriters.add(new MediaType("*", "*"), key);
         } else {
            serverMessageBodyWriters.add(new MediaType("*", "*"), key);
         }
      }
   }

   @Deprecated
   public <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders = getServerMessageBodyReaders();
      return resolveMessageBodyReader(type, genericType, annotations, mediaType, availableReaders);
   }

   public <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, RESTEasyTracingLogger tracingLogger)
   {
      MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders = getServerMessageBodyReaders();
      return resolveMessageBodyReader(type, genericType, annotations, mediaType, availableReaders, tracingLogger);
   }

   /**
    * Always returns server MBRs.
    *
    * @param type        the class of the object that is to be read.
    * @param genericType the type of object to be produced. E.g. if the
    *                    message body is to be converted into a method parameter, this will be
    *                    the formal type of the method parameter as returned by
    *                    {@code Class.getGenericParameterTypes}.
    * @param annotations an array of the annotations on the declaration of the
    *                    artifact that will be initialized with the produced instance. E.g. if
    *                    the message body is to be converted into a method parameter, this will
    *                    be the annotations on that parameter returned by
    *                    {@code Class.getParameterAnnotations}.
    * @param mediaType   the media type of the data that will be read.
    * @param <T> type
    * @return message reader
    */
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders = getServerMessageBodyReaders();
      MessageBodyReader<T> reader = resolveMessageBodyReader(type, genericType, annotations, mediaType, availableReaders);
      if (reader!=null)
          LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
      return reader;
   }

   public <T> MessageBodyReader<T> getClientMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders = getClientMessageBodyReaders();
      return resolveMessageBodyReader(type, genericType, annotations, mediaType, availableReaders);
   }

   @Deprecated
   private <T> MessageBodyReader<T> resolveMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders)
   {
      List<SortedKey<MessageBodyReader>> readers = availableReaders.getPossible(mediaType, type);

      //logger.info("******** getMessageBodyReader *******");
      for (SortedKey<MessageBodyReader> reader : readers)
      {
         //logger.info("     matching reader: " + reader.getClass().getName());
         if (reader.obj.isReadable(type, genericType, annotations, mediaType))
         {
            LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
            return (MessageBodyReader<T>) reader.obj;
         }
      }
      return null;
   }

   protected <T> MessageBodyReader<T> resolveMessageBodyReader(Class<T> type,
                                                               Type genericType,
                                                               Annotation[] annotations,
                                                               MediaType mediaType,
                                                               MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders,
                                                               RESTEasyTracingLogger tracingLogger) {
      List<SortedKey<MessageBodyReader>> readers = availableReaders.getPossible(mediaType, type);

      if (tracingLogger.isLogEnabled("MBR_FIND")) {
         tracingLogger.log("MBR_FIND",
                 type.getName(),
                 (genericType instanceof Class ? ((Class) genericType).getName() : genericType),
                 mediaType,
                 java.util.Arrays.toString(annotations));
      }

      MessageBodyReader<T> result = null;

      Iterator<SortedKey<MessageBodyReader>> iterator = readers.iterator();

      while (iterator.hasNext()) {
         final SortedKey<MessageBodyReader> reader = iterator.next();

         if (reader.obj.isReadable(type, genericType, annotations, mediaType)) {
            LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
            result = (MessageBodyReader<T>) reader.obj;
            tracingLogger.log("MBR_SELECTED", reader);
            break;
         }
         tracingLogger.log("MBR_NOT_READABLE", result);
      }

      if (tracingLogger.isLogEnabled("MBR_SKIPPED")) {
         while (iterator.hasNext()) {
            final SortedKey<MessageBodyReader> reader = iterator.next();
            tracingLogger.log("MBR_SKIPPED", reader.obj);
         }
      }
      return result;
   }

   private void addExceptionMapper(ExceptionMapper provider, Class providerClass, boolean isBuiltin)
   {
      // Check for weld proxy.
      if (providerClass.isSynthetic())
      {
         providerClass = providerClass.getSuperclass();
      }
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(providerClass, ExceptionMapper.class)[0];
      
      injectProperties(providerClass, provider);

      Class<?> exceptionClass = Types.getRawType(exceptionType);
      if (!Throwable.class.isAssignableFrom(exceptionClass))
      {
         throw new RuntimeException(Messages.MESSAGES.incorrectTypeParameterExceptionMapper());
      }
      if (sortedExceptionMappers == null)
      {
         sortedExceptionMappers = new ConcurrentHashMap<Class<?>, SortedKey<ExceptionMapper>>();
         sortedExceptionMappers.putAll(parent.getSortedExceptionMappers());
      }
      int priority = getPriority(null, null, ExceptionMapper.class, providerClass);
      SortedKey<ExceptionMapper> candidateExceptionMapper = new SortedKey<>(null, provider, providerClass, priority, isBuiltin);
      SortedKey<ExceptionMapper> registeredExceptionMapper;
      if ((registeredExceptionMapper = sortedExceptionMappers.get(exceptionClass)) != null
          && (candidateExceptionMapper.compareTo(registeredExceptionMapper) > 0)) {
         return;
      }
      sortedExceptionMappers.put(exceptionClass, candidateExceptionMapper);
      exceptionMappers = null;
   }

   private void addAsyncResponseProvider(AsyncResponseProvider provider, Class providerClass)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncResponseProvider.class)[0];
      injectProperties(provider.getClass(), provider);
      Class<?> asyncClass = Types.getRawType(asyncType);
      if (asyncResponseProviders == null)
      {
          asyncResponseProviders = new ConcurrentHashMap<Class<?>, AsyncResponseProvider>();
          asyncResponseProviders.putAll(parent.getAsyncResponseProviders());
      }
      asyncResponseProviders.put(asyncClass, provider);
   }

   private void addAsyncClientResponseProvider(AsyncClientResponseProvider provider, Class providerClass)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncClientResponseProvider.class)[0];
      injectProperties(provider.getClass(), provider);

      Class<?> asyncClass = Types.getRawType(asyncType);
      if (asyncClientResponseProviders == null)
      {
          asyncClientResponseProviders = new ConcurrentHashMap<Class<?>, AsyncClientResponseProvider>();
          asyncClientResponseProviders.putAll(parent.getAsyncClientResponseProviders());
      }
      asyncClientResponseProviders.put(asyncClass, provider);
   }
   
   private void addAsyncStreamProvider(AsyncStreamProvider provider, Class providerClass)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncStreamProvider.class)[0];
      injectProperties(provider.getClass(), provider);
      Class<?> asyncClass = Types.getRawType(asyncType);
      if (asyncStreamProviders == null)
      {
          asyncStreamProviders = new ConcurrentHashMap<Class<?>, AsyncStreamProvider>();
          asyncStreamProviders.putAll(parent.getAsyncStreamProviders());
      }
      asyncStreamProviders.put(asyncClass, provider);
   }

   private void addContextInjector(ContextInjector provider, Class providerClass)
   {
      Type[] typeArgs = Types.getActualTypeArgumentsOfAnInterface(providerClass, ContextInjector.class);
      injectProperties(provider.getClass(), provider);

      if (contextInjectors == null)
      {
         contextInjectors = new ConcurrentHashMap<Type, ContextInjector>();
         contextInjectors.putAll(parent.getContextInjectors());
      }
      contextInjectors.put(typeArgs[0], provider);
      
      if(!Objects.equals(typeArgs[0], typeArgs[1]))
      {
         if (asyncContextInjectors == null)
         {
            asyncContextInjectors = new ConcurrentHashMap<Type, ContextInjector>();
            asyncContextInjectors.putAll(parent.getAsyncContextInjectors());
         }
         asyncContextInjectors.put(typeArgs[1], provider);
      }
   }

   private void addContextResolver(ContextResolver provider, Class providerClass, boolean builtin)
   {
      // RESTEASY-1725
      if (providerClass.getName().contains("$$Lambda$")) {
         throw new RuntimeException(Messages.MESSAGES.registeringContextResolverAsLambda());
      }
      Type typeParameter = Types.getActualTypeArgumentsOfAnInterface(providerClass, ContextResolver.class)[0];
      injectProperties(providerClass, provider);
      Class<?> parameterClass = Types.getRawType(typeParameter);
      if (contextResolvers == null)
      {
         contextResolvers = new ConcurrentHashMap<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>>();
         for (Map.Entry<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> entry : parent.getContextResolvers().entrySet())
         {
            contextResolvers.put(entry.getKey(), entry.getValue().clone());
         }
      }
      MediaTypeMap<SortedKey<ContextResolver>> resolvers = contextResolvers.get(parameterClass);
      if (resolvers == null)
      {
         resolvers = new MediaTypeMap<SortedKey<ContextResolver>>();
         contextResolvers.put(parameterClass, resolvers);
      }
      Produces produces = provider.getClass().getAnnotation(Produces.class);
      int priority = this.getPriority(null,  null,  ContextResolver.class, providerClass);
      SortedKey<ContextResolver> key = new SortedKey<ContextResolver>(ContextResolver.class, provider, providerClass, priority, builtin);
      if (produces != null)
      {
         for (String produce : produces.value())
         {
            MediaType mime = MediaType.valueOf(produce);
            resolvers.add(mime, key);
         }
      }
      else
      {
         resolvers.add(new MediaType("*", "*"), key);
      }
   }

   private void addStringParameterUnmarshaller(Class<? extends StringParameterUnmarshaller> provider)
   {
      if (stringParameterUnmarshallers == null)
      {
         stringParameterUnmarshallers = new ConcurrentHashMap<Class<?>, Class<? extends StringParameterUnmarshaller>>();
         stringParameterUnmarshallers.putAll(parent.getStringParameterUnmarshallers());
      }
      Type[] intfs = provider.getGenericInterfaces();
      for (Type type : intfs)
      {
         if (type instanceof ParameterizedType)
         {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType().equals(StringParameterUnmarshaller.class))
            {
               Class<?> aClass = Types.getRawType(pt.getActualTypeArguments()[0]);
               stringParameterUnmarshallers.put(aClass, provider);
            }
         }
      }
   }

   public List<ContextResolver> getContextResolvers(final Class<?> clazz, MediaType type)
   {
      MediaTypeMap<SortedKey<ContextResolver>> resolvers = getContextResolvers().get(clazz);
      if (resolvers == null) return null;
      List<ContextResolver> rtn = new ArrayList<ContextResolver>();

      List<SortedKey<ContextResolver>> list = resolvers.getPossible(type);
      if (type.isWildcardType())
      {
         // do it upside down if it is a wildcard type:  Note: this is to pass the stupid TCK which prefers that
         // a wildcard type match up with other wildcard types
//         for (int i = list.size() - 1; i >= 0; i--)
//         {
//            rtn.add(list.get(i).obj);
//         }

         // Fix for RESTEASY-1609.
         // This is related to the fix in RESTEASY-1471, prior to which user ContextResolvers appeared
         // to be built-in. The original loop may have been in response to that bug, so the reversal
         // may not be necessary. In any case, this code will do the reversal but put user ContextResolvers
         // at the beginning of the list.
         for (int i = list.size() - 1; i >= 0; i--)
         {
            if (!list.get(i).isBuiltin)
            {
               rtn.add(list.get(i).obj);
            }
         }
         for (int i = list.size() - 1; i >= 0; i--)
         {
            if (list.get(i).isBuiltin)
            {
               rtn.add(list.get(i).obj);
            }
         }
      }
      else
      {
         for (SortedKey<ContextResolver> resolver : list)
         {
            rtn.add(resolver.obj);
         }
      }
      return rtn;
   }

   public ParamConverter getParamConverter(Class clazz, Type genericType, Annotation[] annotations)
   {
      for (SortedKey<ParamConverterProvider> provider : getSortedParamConverterProviders())
      {
         ParamConverter converter = provider.getObj().getConverter(clazz, genericType, annotations);
         if (converter != null) return converter;
      }
      return null;
   }

   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      if (getStringParameterUnmarshallers().size() == 0) return null;
      Class<? extends StringParameterUnmarshaller> un = getStringParameterUnmarshallers().get(clazz);
      if (un == null) return null;
      StringParameterUnmarshaller<T> provider = injectedInstance(un);
      return provider;

   }

   public void registerProvider(Class provider)
   {
      registerProvider(provider, false);
   }

   /**
    * Convert an object to a string.  First try StringConverter then, object.ToString()
    *
    * @param object object
    * @param clazz class
    * @param genericType generic type
    * @param annotations array of annotation
    * @return string representation
    */
   public String toString(Object object, Class clazz, Type genericType, Annotation[] annotations)
   {
      if (object instanceof String)
         return (String) object;
      ParamConverter paramConverter = getParamConverter(clazz, genericType, annotations);
      if (paramConverter != null)
      {
         return paramConverter.toString(object);
      }
      return object.toString();
   }

   @Override
   public String toHeaderString(Object object)
   {
      if (object == null) return "";
      if (object instanceof String) return (String) object;
      Class<?> aClass = object.getClass();
      ParamConverter paramConverter = getParamConverter(aClass, null, null);
      if (paramConverter != null)
      {
         return paramConverter.toString(object);
      }
      HeaderDelegate delegate = getHeaderDelegate(aClass);
      if (delegate != null)
         return delegate.toString(object);
      else
         return object.toString();

   }

   /**
    * Checks to see if RuntimeDelegate is a ResteasyProviderFactory
    * If it is, then use that, otherwise use this.
    *
    * @param aClass class of the header
    * @return header delegate
    */
   public HeaderDelegate getHeaderDelegate(Class<?> aClass)
   {
      HeaderDelegate delegate = null;
      // Stupid idiotic TCK calls RuntimeDelegate.setInstance()
      if (RuntimeDelegate.getInstance() instanceof ResteasyProviderFactory)
      {
         delegate = createHeaderDelegate(aClass);
      }
      else
      {
         delegate = RuntimeDelegate.getInstance().createHeaderDelegate(aClass);
      }
      return delegate;
   }

   /**
    * Register a @Provider class.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider provider class
    * @param isBuiltin built-in
    */
   public void registerProvider(Class provider, boolean isBuiltin)
   {
      registerProvider(provider, null, isBuiltin, null);
   }

   protected static boolean isA(Class target, Class type, Map<Class<?>, Integer> contracts)
   {
      if (!type.isAssignableFrom(target)) return false;
      if (contracts == null || contracts.size() == 0) return true;
      for (Class<?> contract : contracts.keySet())
      {
         if (contract.equals(type)) return true;
      }
      return false;
   }

   protected static boolean isA(Object target, Class type, Map<Class<?>, Integer> contracts)
   {
      return isA(target.getClass(), type, contracts);
   }

   private static int getPriority(Integer override, Map<Class<?>, Integer> contracts, Class type, Class<?> component)
   {
      if (override != null) return override;
      if (contracts != null)
      {
         Integer p = contracts.get(type);
         if (p != null) return p;
      }
      // Check for weld proxy.
      component = component.isSynthetic() ? component.getSuperclass() : component;
      Priority priority = component.getAnnotation(Priority.class);
      if (priority == null) return Priorities.USER;
      return priority.value();
   }

   public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts)
   {
      Map<Class<?>, Map<Class<?>, Integer>> classContracts = getClassContracts();
      if (classContracts.containsKey(provider))
      {
         LogMessages.LOGGER.providerClassAlreadyRegistered(provider.getName());
         return;
      }
      Map<Class<?>, Integer> newContracts = new HashMap<Class<?>, Integer>();
      processProviderContracts(provider, priorityOverride, isBuiltin, contracts, newContracts);
      providerClasses.add(provider);
      classContracts.put(provider, newContracts);
   }

   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
         Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts)
   {
      if (isA(provider, ParamConverterProvider.class, contracts))
      {
         ParamConverterProvider paramConverterProvider = (ParamConverterProvider) injectedInstance(provider);
         injectProperties(provider);
         if (sortedParamConverterProviders == null)
         {
            sortedParamConverterProviders = Collections.synchronizedSortedSet(new TreeSet<>(parent.getSortedParamConverterProviders()));
         }
         int priority = getPriority(priorityOverride, contracts, ParamConverterProvider.class, provider);
         sortedParamConverterProviders.add(new ExtSortedKey<>(null, paramConverterProvider, provider, priority, isBuiltin));
         newContracts.put(ParamConverterProvider.class, priority);
      }
      if (isA(provider, MessageBodyReader.class, contracts))
      {
         try
         {
            int priority = getPriority(priorityOverride, contracts, MessageBodyReader.class, provider);
            addMessageBodyReader(createProviderInstance((Class<? extends MessageBodyReader>)provider), provider, priority, isBuiltin);
            newContracts.put(MessageBodyReader.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyReader(), e);
         }
      }
      if (isA(provider, MessageBodyWriter.class, contracts))
      {
         try
         {
            int priority = getPriority(priorityOverride, contracts, MessageBodyWriter.class, provider);
            addMessageBodyWriter(createProviderInstance((Class<? extends MessageBodyWriter>)provider), provider, priority, isBuiltin);
            newContracts.put(MessageBodyWriter.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyWriter(), e);
         }
      }
      if (isA(provider, ExceptionMapper.class, contracts))
      {
         try
         {
            addExceptionMapper(createProviderInstance((Class<? extends ExceptionMapper>) provider), provider, isBuiltin);
            newContracts.put(ExceptionMapper.class, getPriority(priorityOverride, contracts, ExceptionMapper.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateExceptionMapper(), e);
         }
      }
      if (isA(provider, AsyncResponseProvider.class, contracts))
      {
         try
         {
            addAsyncResponseProvider(createProviderInstance((Class<? extends AsyncResponseProvider>) provider), provider);
            newContracts.put(AsyncResponseProvider.class, getPriority(priorityOverride, contracts, AsyncResponseProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncResponseProvider(), e);
         }
      }
      if (isA(provider, AsyncClientResponseProvider.class, contracts))
      {
         try
         {
            addAsyncClientResponseProvider(createProviderInstance((Class<? extends AsyncClientResponseProvider>) provider), provider);
            newContracts.put(AsyncClientResponseProvider.class, getPriority(priorityOverride, contracts, AsyncClientResponseProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncClientResponseProvider(), e);
         }
      }
      if (isA(provider, AsyncStreamProvider.class, contracts))
      {
         try
         {
            addAsyncStreamProvider(createProviderInstance((Class<? extends AsyncStreamProvider>)provider), provider);
            newContracts.put(AsyncStreamProvider.class, getPriority(priorityOverride, contracts, AsyncStreamProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncStreamProvider(), e);
         }
      }
      if (isA(provider, ClientRequestFilter.class, contracts))
      {
         if (clientRequestFilterRegistry == null)
         {
            clientRequestFilterRegistry = parent.getClientRequestFilterRegistry().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ClientRequestFilter.class, provider);
         clientRequestFilterRegistry.registerClass(provider, priority);
         newContracts.put(ClientRequestFilter.class, priority);
      }
      if (isA(provider, ClientResponseFilter.class, contracts))
      {
         if (clientResponseFilters == null)
         {
            clientResponseFilters = parent.getClientResponseFilters().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ClientResponseFilter.class, provider);
         clientResponseFilters.registerClass(provider, priority);
         newContracts.put(ClientResponseFilter.class, priority);
      }
      if (isA(provider, ContainerRequestFilter.class, contracts))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ContainerRequestFilter.class, provider);
         containerRequestFilterRegistry.registerClass(provider, priority);
         newContracts.put(ContainerRequestFilter.class, priority);
      }
      if (isA(provider, ContainerResponseFilter.class, contracts))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ContainerResponseFilter.class, provider);
         containerResponseFilterRegistry.registerClass(provider, priority);
         newContracts.put(ContainerResponseFilter.class, priority);
      }
      if (isA(provider, ReaderInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, ReaderInterceptor.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerClass(provider, priority);
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerClass(provider, priority);
         }
         newContracts.put(ReaderInterceptor.class, priority);
      }
      if (isA(provider, WriterInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, WriterInterceptor.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerClass(provider, priority);
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerClass(provider, priority);
         }
         newContracts.put(WriterInterceptor.class, priority);
      }
      if (isA(provider, ContextResolver.class, contracts))
      {
         try
         {
            addContextResolver(createProviderInstance((Class<? extends ContextResolver>)provider), provider, isBuiltin);
            int priority = getPriority(priorityOverride, contracts, ContextResolver.class, provider);
            newContracts.put(ContextResolver.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateContextResolver(), e);
         }
      }
      if (isA(provider, ContextInjector.class, contracts))
      {
         try
         {
            addContextInjector(createProviderInstance((Class<? extends ContextInjector>) provider), provider);
            int priority = getPriority(priorityOverride, contracts, ContextInjector.class, provider);
            newContracts.put(ContextInjector.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateContextInjector(), e);
         }
      }
      if (isA(provider, StringParameterUnmarshaller.class, contracts))
      {
         addStringParameterUnmarshaller(provider);
         int priority = getPriority(priorityOverride, contracts, StringParameterUnmarshaller.class, provider);
         newContracts.put(StringParameterUnmarshaller.class, priority);
      }
      if (isA(provider, InjectorFactory.class, contracts))
      {
         try
         {
            this.injectorFactory = (InjectorFactory) provider.newInstance();
            newContracts.put(InjectorFactory.class, 0);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      if (isA(provider, DynamicFeature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, DynamicFeature.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) injectedInstance(provider));
         }
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) injectedInstance(provider));
         }
         if (constrainedTo == null)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) injectedInstance(provider));
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) injectedInstance(provider));
         }
         newContracts.put(DynamicFeature.class, priority);
      }
      if (isA(provider, Feature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, Feature.class, provider);
         Feature feature = injectedInstance((Class<? extends Feature>) provider);
         if (constrainedTo == null || constrainedTo.value() == getRuntimeType()) {
            if (feature.configure(new FeatureContextDelegate(this)))
            {
               enabledFeatures.add(feature);
            }
         }
         newContracts.put(Feature.class, priority);
      }
      if (isA(provider, RxInvokerProvider.class, contracts))
      {
         int priority = getPriority(priorityOverride, contracts, RxInvokerProvider.class, provider);
         newContracts.put(RxInvokerProvider.class, priority);
         Class<?> clazz = Types.getTemplateParameterOfInterface(provider, RxInvokerProvider.class);
         clazz = Types.getTemplateParameterOfInterface(clazz, RxInvoker.class); 
         if (clazz != null)
         {
            reactiveClasses.put(clazz, provider);
         }
      }
      if (isA(provider, ResourceClassProcessor.class, contracts))
      {
         int priority = getPriority(priorityOverride, contracts, ResourceClassProcessor.class, provider);
         addResourceClassProcessor(provider, priority);
         newContracts.put(ResourceClassProcessor.class, priority);
      }
   }

   /**
    * Register a @Provider object.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider provider instance
    */
   public void registerProviderInstance(Object provider)
   {
      registerProviderInstance(provider, null, null, false);
   }

   public void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts, Integer priorityOverride, boolean builtIn)
   {
      Class<?> providerClass=provider.getClass();
      Map<Class<?>, Map<Class<?>, Integer>> classContracts = getClassContracts();
      if (classContracts.containsKey(providerClass))
      {
         LogMessages.LOGGER.providerInstanceAlreadyRegistered(providerClass.getName());
         return;
      }
      Map<Class<?>, Integer> newContracts = new HashMap<Class<?>, Integer>();
      processProviderInstanceContracts(provider, contracts, priorityOverride, builtIn, newContracts);
      providerInstances.add(provider);
      classContracts.put(providerClass, newContracts);
   }

   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
         Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts)
   {
      if (isA(provider, ParamConverterProvider.class, contracts))
      {
         injectProperties(provider);
         if (sortedParamConverterProviders == null)
         {
            sortedParamConverterProviders = Collections.synchronizedSortedSet(new TreeSet<>(parent.getSortedParamConverterProviders()));
         }
         int priority = getPriority(priorityOverride, contracts, ParamConverterProvider.class, provider.getClass());
         sortedParamConverterProviders.add(new ExtSortedKey<>(null, (ParamConverterProvider) provider, provider.getClass(), priority, builtIn));
         newContracts.put(ParamConverterProvider.class, priority);
      }
      if (isA(provider, MessageBodyReader.class, contracts))
      {
         try
         {
            int priority = getPriority(priorityOverride, contracts, MessageBodyReader.class, provider.getClass());
            addMessageBodyReader((MessageBodyReader) provider, provider.getClass(), priority, builtIn);
            newContracts.put(MessageBodyReader.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyReader(), e);
         }
      }
      if (isA(provider, MessageBodyWriter.class, contracts))
      {
         try
         {
            int priority = getPriority(priorityOverride, contracts, MessageBodyWriter.class, provider.getClass());
            addMessageBodyWriter((MessageBodyWriter) provider, provider.getClass(), priority, builtIn);
            newContracts.put(MessageBodyWriter.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyWriter(), e);
         }
      }
      if (isA(provider, ExceptionMapper.class, contracts))
      {
         try
         {
            addExceptionMapper((ExceptionMapper) provider, provider.getClass(), builtIn);
            int priority = getPriority(priorityOverride, contracts, ExceptionMapper.class, provider.getClass());
            newContracts.put(ExceptionMapper.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateExceptionMapper(), e);
         }
      }
      if (isA(provider, AsyncResponseProvider.class, contracts))
      {
         try
         {
            addAsyncResponseProvider((AsyncResponseProvider) provider, provider.getClass());
            int priority = getPriority(priorityOverride, contracts, AsyncResponseProvider.class, provider.getClass());
            newContracts.put(AsyncResponseProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncResponseProvider(), e);
         }
      }
      if (isA(provider, AsyncClientResponseProvider.class, contracts))
      {
         try
         {
            addAsyncClientResponseProvider((AsyncClientResponseProvider) provider, provider.getClass());
            int priority = getPriority(priorityOverride, contracts, AsyncClientResponseProvider.class, provider.getClass());
            newContracts.put(AsyncClientResponseProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncClientResponseProvider(), e);
         }
      }
      if (isA(provider, AsyncStreamProvider.class, contracts))
      {
         try
         {
            addAsyncStreamProvider((AsyncStreamProvider) provider, provider.getClass());
            int priority = getPriority(priorityOverride, contracts, AsyncStreamProvider.class, provider.getClass());
            newContracts.put(AsyncStreamProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncStreamProvider(), e);
         }
      }
      if (isA(provider, ContextResolver.class, contracts))
      {
         try
         {
            addContextResolver((ContextResolver) provider, provider.getClass(), false);
            int priority = getPriority(priorityOverride, contracts, ContextResolver.class, provider.getClass());
            newContracts.put(ContextResolver.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateContextResolver(), e);
         }
      }
      if (isA(provider, ContextInjector.class, contracts))
      {
         try
         {
            addContextInjector((ContextInjector)provider, provider.getClass());
            int priority = getPriority(priorityOverride, contracts, ContextInjector.class, provider.getClass());
            newContracts.put(ContextInjector.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateContextInjector(), e);
         }
      }
      if (isA(provider, ClientRequestFilter.class, contracts))
      {
         if (clientRequestFilterRegistry == null)
         {
            clientRequestFilterRegistry = parent.getClientRequestFilterRegistry().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ClientRequestFilter.class, provider.getClass());
         clientRequestFilterRegistry.registerSingleton((ClientRequestFilter) provider, priority);
         newContracts.put(ClientRequestFilter.class, priority);
      }
      if (isA(provider, ClientResponseFilter.class, contracts))
      {
         if (clientResponseFilters == null)
         {
            clientResponseFilters = parent.getClientResponseFilters().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ClientResponseFilter.class, provider.getClass());
         clientResponseFilters.registerSingleton((ClientResponseFilter) provider, priority);
         newContracts.put(ClientResponseFilter.class, priority);
      }
      if (isA(provider, ContainerRequestFilter.class, contracts))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ContainerRequestFilter.class, provider.getClass());
         containerRequestFilterRegistry.registerSingleton((ContainerRequestFilter) provider, priority);
         newContracts.put(ContainerRequestFilter.class, priority);
      }
      if (isA(provider, ContainerResponseFilter.class, contracts))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(this);
         }
         int priority = getPriority(priorityOverride, contracts, ContainerResponseFilter.class, provider.getClass());
         containerResponseFilterRegistry.registerSingleton((ContainerResponseFilter) provider, priority);
         newContracts.put(ContainerResponseFilter.class, priority);
      }
      if (isA(provider, ReaderInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, ReaderInterceptor.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         newContracts.put(ReaderInterceptor.class, priority);
      }
      if (isA(provider, WriterInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, WriterInterceptor.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         newContracts.put(WriterInterceptor.class, priority);
      }
      if (isA(provider, InjectorFactory.class, contracts))
      {
         this.injectorFactory = (InjectorFactory) provider;
         newContracts.put(InjectorFactory.class, 0);
      }
      if (isA(provider, DynamicFeature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = getPriority(priorityOverride, contracts, DynamicFeature.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) provider);
         }
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) provider);
         }
         if (constrainedTo == null)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) provider);
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) provider);
         }
         newContracts.put(DynamicFeature.class, priority);
      }
      if (isA(provider, Feature.class, contracts))
      {
         Feature feature = (Feature) provider;
         injectProperties(provider.getClass(), provider);
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         if (constrainedTo == null || constrainedTo.value() == getRuntimeType()) {
            if (feature.configure(new FeatureContextDelegate(this)))
            {
               enabledFeatures.add(feature);
            }
         }
         int priority = getPriority(priorityOverride, contracts, Feature.class, provider.getClass());
         newContracts.put(Feature.class, priority);

      }
      if (isA(provider, ResourceClassProcessor.class, contracts))
      {
         int priority = getPriority(priorityOverride, contracts, ResourceClassProcessor.class, provider.getClass());
         addResourceClassProcessor((ResourceClassProcessor) provider, priority);
         newContracts.put(ResourceClassProcessor.class, priority);
      }
   }

   @Override
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      Class exceptionType = type;
      SortedKey<ExceptionMapper> mapper = null;
      while (mapper == null)
      {
         if (exceptionType == null) break;
         mapper = getSortedExceptionMappers().get(exceptionType);
         if (mapper == null) exceptionType = exceptionType.getSuperclass();
      }
      return mapper != null ? mapper.getObj() : null;
   }

//   @Override
   public <T> AsyncResponseProvider<T> getAsyncResponseProvider(Class<T> type)
   {
      Class asyncType = type;
      AsyncResponseProvider<T> mapper = null;
      while (mapper == null)
      {
         if (asyncType == null) break;
         mapper = getAsyncResponseProviders().get(asyncType);
         if (mapper == null) asyncType = asyncType.getSuperclass();
      }
      return mapper;
   }
   
   public <T> AsyncClientResponseProvider<T> getAsyncClientResponseProvider(Class<T> type)
   {
      Class asyncType = type;
      AsyncClientResponseProvider<T> mapper = null;
      while (mapper == null)
      {
         if (asyncType == null) break;
         mapper = getAsyncClientResponseProviders().get(asyncType);
         if (mapper == null) asyncType = asyncType.getSuperclass();
      }
      return mapper;
   }

   // @Override
   public <T> AsyncStreamProvider<T> getAsyncStreamProvider(Class<T> type)
   {
      Class asyncType = type;
      AsyncStreamProvider<T> mapper = null;
      while (mapper == null)
      {
         if (asyncType == null) break;
         mapper = getAsyncStreamProviders().get(asyncType);
         if (mapper == null) asyncType = asyncType.getSuperclass();
      }
      return mapper;
   }

   public MediaType getConcreteMediaTypeFromMessageBodyWriters(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      List<SortedKey<MessageBodyWriter>> writers = getServerMessageBodyWriters().getPossible(mediaType, type);
      for (SortedKey<MessageBodyWriter> writer : writers)
      {
         if (writer.obj.isWriteable(type, genericType, annotations, mediaType))
         {
            MessageBodyWriter mbw = writer.obj;
            Class writerType = Types.getTemplateParameterOfInterface(mbw.getClass(), MessageBodyWriter.class);
            if (writerType == null || writerType.equals(Object.class) || !writerType.isAssignableFrom(type)) continue;
            Produces produces = mbw.getClass().getAnnotation(Produces.class);
            if (produces == null) continue;
            for (String produce : produces.value())
            {
               MediaType mt = MediaType.valueOf(produce);
               if (mt.isWildcardType() || mt.isWildcardSubtype()) continue;
               return mt;
            }
         }
      }
      return null;
   }

   public Map<MessageBodyWriter<?>, Class<?>> getPossibleMessageBodyWritersMap(Class type, Type genericType, Annotation[] annotations, MediaType accept)
   {
      Map<MessageBodyWriter<?>, Class<?>> map = new HashMap<MessageBodyWriter<?>, Class<?>>();
      List<SortedKey<MessageBodyWriter>> writers = getServerMessageBodyWriters().getPossible(accept, type);
      for (SortedKey<MessageBodyWriter> writer : writers)
      {
         if (writer.obj.isWriteable(type, genericType, annotations, accept))
         {
            Class<?> mbwc = writer.obj.getClass();
            if (!mbwc.isInterface() && mbwc.getSuperclass() != null && !mbwc.getSuperclass().equals(Object.class) && mbwc.isSynthetic()) {
               mbwc = mbwc.getSuperclass();
            }
            Class writerType = Types.getTemplateParameterOfInterface(mbwc, MessageBodyWriter.class);
            if (writerType == null || !writerType.isAssignableFrom(type)) continue;
            map.put(writer.obj, writerType);
         }
      }
      return map;
   }

   // use the tracingLogger enabled version please
   @Deprecated
   public <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters = getServerMessageBodyWriters();
      return resolveMessageBodyWriter(type, genericType, annotations, mediaType, availableWriters);
   }


   public <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, RESTEasyTracingLogger tracingLogger) {

      MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters = getServerMessageBodyWriters();
      return resolveMessageBodyWriter(type, genericType, annotations, mediaType, availableWriters, tracingLogger);
   }

   /**
    * Always gets server MBW.
    *
    * @param type        the class of the object that is to be written.
    * @param genericType the type of object to be written. E.g. if the
    *                    message body is to be produced from a field, this will be
    *                    the declared type of the field as returned by {@code Field.getGenericType}.
    * @param annotations an array of the annotations on the declaration of the
    *                    artifact that will be written. E.g. if the
    *                    message body is to be produced from a field, this will be
    *                    the annotations on that field returned by
    *                    {@code Field.getDeclaredAnnotations}.
    * @param mediaType   the media type of the data that will be written.
    * @param <T> type
    * @return message writer
    */
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters = getServerMessageBodyWriters();
      MessageBodyWriter<T> writer = resolveMessageBodyWriter(type, genericType, annotations, mediaType, availableWriters);
      if (writer!=null)
          LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
      return writer;
   }

   public <T> MessageBodyWriter<T> getClientMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters = getClientMessageBodyWriters();
      return resolveMessageBodyWriter(type, genericType, annotations, mediaType, availableWriters);
   }

   @Deprecated
   private <T> MessageBodyWriter<T> resolveMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters)
   {
      List<SortedKey<MessageBodyWriter>> writers = availableWriters.getPossible(mediaType, type);
      /*
      logger.info("*******   getMessageBodyWriter(" + type.getName() + ", " + mediaType.toString() + ")****");
      for (SortedKey<MessageBodyWriter> writer : writers)
      {
         logger.info("     possible writer: " + writer.obj.getClass().getName());
      }
      */

      for (SortedKey<MessageBodyWriter> writer : writers)
      {
         if (writer.obj.isWriteable(type, genericType, annotations, mediaType))
         {
            LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
            //logger.info("   picking: " + writer.obj.getClass().getName());
            return (MessageBodyWriter<T>) writer.obj;
         }
      }
      return null;
   }

   protected <T> MessageBodyWriter<T> resolveMessageBodyWriter(Class<T> type,
                                                               Type genericType,
                                                               Annotation[] annotations,
                                                               MediaType mediaType,
                                                               MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters,
                                                               RESTEasyTracingLogger tracingLogger) {
      List<SortedKey<MessageBodyWriter>> writers = availableWriters.getPossible(mediaType, type);

      if (tracingLogger.isLogEnabled("MBW_FIND")) {
         tracingLogger.log("MBW_FIND",
                 type.getName(),
                 (genericType instanceof Class
                         ? ((Class) genericType).getName() : genericType),
                 mediaType,
                 java.util.Arrays.toString(annotations));
      }

      MessageBodyWriter<T> result = null;

      Iterator<SortedKey<MessageBodyWriter>> iterator = writers.iterator();

      while (iterator.hasNext()) {
         final SortedKey<MessageBodyWriter> writer = iterator.next();
         if (writer.obj.isWriteable(type, genericType, annotations, mediaType)) {
            LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
            result = (MessageBodyWriter<T>) writer.obj;
            tracingLogger.log("MBW_SELECTED", result);
            break;
         }
         tracingLogger.log("MBW_NOT_WRITEABLE", result);
      }

      if (tracingLogger.isLogEnabled("MBW_SKIPPED")) {
         while (iterator.hasNext()) {
            final SortedKey<MessageBodyWriter> writer = iterator.next();
            tracingLogger.log("MBW_SKIPPED", writer.obj);
         }
      }
      return result;
   }



   /**
    * This is a spec method that is unsupported.  It is an optional method anyways.
    *
    * @param applicationConfig application
    * @param endpointType endpoint type
    * @return endpoint
    * @throws IllegalArgumentException if applicationConfig is null
    * @throws UnsupportedOperationException allways throw since this method is not supported
    */
   public <T> T createEndpoint(Application applicationConfig, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException
   {
      if (applicationConfig == null) throw new IllegalArgumentException(Messages.MESSAGES.applicationParamNull());
      throw new UnsupportedOperationException();
   }

   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      final List<ContextResolver> resolvers = getContextResolvers(contextType, mediaType);
      if (resolvers == null) return null;
      if (resolvers.size() == 1) return resolvers.get(0);
      return new ContextResolver<T>()
      {
         public T getContext(Class type)
         {
            for (ContextResolver resolver : resolvers)
            {
               Object rtn = resolver.getContext(type);
               if (rtn != null) return (T) rtn;
            }
            return null;
         }
      };
   }

   /**
    * Create an instance of a class using provider allocation rules of the specification as well as the InjectorFactory
    * only does constructor injection.
    *
    * @param clazz class 
    * @param <T> type
    * @return provider instance of type T
    */
   public <T> T createProviderInstance(Class<? extends T> clazz)
   {
      ConstructorInjector constructorInjector = createConstructorInjector(clazz);

      T provider = (T) constructorInjector.construct(false).toCompletableFuture().getNow(null);
      return provider;
   }

   private <T> ConstructorInjector createConstructorInjector(Class<? extends T> clazz)
   {
      Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
      if (constructor == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.unableToFindPublicConstructorForProvider(clazz.getName()));
      }
      return getInjectorFactory().createConstructor(constructor, this);
   }

   /**
    * Property and constructor injection using the InjectorFactory.
    *
    * @param clazz class
    * @param <T> type
    * @return instance of type T
    */
   public <T> T injectedInstance(Class<? extends T> clazz)
   {
      Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
      ConstructorInjector constructorInjector = getInjectorFactory().createConstructor(constructor, this);
      return (T)constructorInjector.construct(false)
      .thenCompose(obj -> {
         PropertyInjector propertyInjector = getInjectorFactory().createPropertyInjector(clazz, this);
         return propertyInjector.inject(obj, false).thenApply(val -> obj);
      }).toCompletableFuture().getNow(null);
   }

   /**
    * Property and constructor injection using the InjectorFactory.
    *
    * @param clazz class
    * @param request http request
    * @param response http response
    * @param <T> type
    * @return instance of type T
    */
   public <T> T injectedInstance(Class<? extends T> clazz, HttpRequest request, HttpResponse response)
   {
      Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
      Object obj = null;
      if (constructor == null)
      {
         // TODO this is solely to pass the TCK.  This is WRONG WRONG WRONG!  I'm challenging.
         if (false)//if (clazz.isAnonymousClass())
         {
            constructor = clazz.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            if (!Modifier.isStatic(clazz.getModifiers()))
            {
               Object[] args = {null};
               try
               {
                  obj = constructor.newInstance(args);
               }
               catch (InstantiationException e)
               {
                  throw new RuntimeException(e);
               }
               catch (IllegalAccessException e)
               {
                  throw new RuntimeException(e);
               }
               catch (InvocationTargetException e)
               {
                  throw new RuntimeException(e);
               }
            }
            else
            {
               try
               {
                  obj = constructor.newInstance();
               }
               catch (InstantiationException e)
               {
                  throw new RuntimeException(e);
               }
               catch (IllegalAccessException e)
               {
                  throw new RuntimeException(e);
               }
               catch (InvocationTargetException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
         else
         {
            throw new IllegalArgumentException(Messages.MESSAGES.unableToFindPublicConstructorForClass(clazz.getName()));
         }
      }
      else
      {
         ConstructorInjector constructorInjector = getInjectorFactory().createConstructor(constructor, this);
         obj = constructorInjector.construct(request, response, false).toCompletableFuture().getNow(null);

      }
      PropertyInjector propertyInjector = getInjectorFactory().createPropertyInjector(clazz, this);

      propertyInjector.inject(request, response, obj, false).toCompletableFuture().getNow(null);
      return (T)obj;
   }

   private void injectProperties(Class declaring, Object obj)
   {
      getInjectorFactory().createPropertyInjector(declaring, this).inject(obj, false).toCompletableFuture().getNow(null);
   }

   public void injectProperties(Object obj)
   {
      getInjectorFactory().createPropertyInjector(obj.getClass(), this).inject(obj, false).toCompletableFuture().getNow(null);
   }
   
   public void injectProperties(Object obj, HttpRequest request, HttpResponse response)
   {
      getInjectorFactory().createPropertyInjector(obj.getClass(), this).inject(request, response, obj, false).toCompletableFuture().getNow(null);
   }

   // Configurable
   public Map<String, Object> getMutableProperties()
   {
      return properties;
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return Collections.unmodifiableMap(properties);
   }

   @Override
   public Object getProperty(String name)
   {
      return properties.get(name);
   }

   public ResteasyProviderFactory setProperties(Map<String, ?> properties)
   {
      Map<String, Object> newProp = new ConcurrentHashMap<String, Object>();
      newProp.putAll(properties);
      this.properties = newProp;
      return this;
   }

   @Override
   public ResteasyProviderFactory property(String name, Object value)
   {
      if (value == null)
         properties.remove(name);
      else
         properties.put(name, value);
      return this;
   }

   public Collection<Feature> getEnabledFeatures()
   {
      if (enabledFeatures == null && parent != null) return parent.getEnabledFeatures();
      Set<Feature> set = new HashSet<Feature>();
      if (parent != null) set.addAll(parent.getEnabledFeatures());
      set.addAll(enabledFeatures);
      return set;
   }

   @Override
   public ResteasyProviderFactory register(Class<?> providerClass)
   {
      registerProvider(providerClass);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Object provider)
   {
      registerProviderInstance(provider);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, int priority)
   {
      registerProvider(componentClass, priority, false, null);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, Class<?>... contracts)
   {
      if (contracts == null || contracts.length == 0)
      {
         LogMessages.LOGGER.attemptingToRegisterEmptyContracts(componentClass.getName());
         return this;
      }
      Map<Class<?>, Integer> cons = new HashMap<Class<?>, Integer>();
      for (Class<?> contract : contracts)
      {
         if (!contract.isAssignableFrom(componentClass))
         {
            LogMessages.LOGGER.attemptingToRegisterUnassignableContract(componentClass.getName());
            return this;
         }
         cons.put(contract, Priorities.USER);
      }
      registerProvider(componentClass, null, false, cons);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Object component, int priority)
   {
      registerProviderInstance(component, null, priority, false);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Object component, Class<?>... contracts)
   {
      if (contracts == null || contracts.length == 0)
      {
         LogMessages.LOGGER.attemptingToRegisterEmptyContracts(component.getClass().getName());
         return this;
      }
      Map<Class<?>, Integer> cons = new HashMap<Class<?>, Integer>();
      for (Class<?> contract : contracts)
      {
         if (!contract.isAssignableFrom(component.getClass()))
         {
            LogMessages.LOGGER.attemptingToRegisterUnassignableContract(component.getClass().getName());
            return this;
         }
         cons.put(contract, Priorities.USER);
      }
      registerProviderInstance(component, cons, null, false);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      for (Class<?> contract : contracts.keySet())
      {
         if (!contract.isAssignableFrom(componentClass))
         {
            LogMessages.LOGGER.attemptingToRegisterUnassignableContract(componentClass.getName());
            return this;
         }
      }
      registerProvider(componentClass, null, false, contracts);
      return this;
   }

   @Override
   public ResteasyProviderFactory register(Object component, Map<Class<?>, Integer> contracts)
   {
      for (Class<?> contract : contracts.keySet())
      {
         if (!contract.isAssignableFrom(component.getClass()))
         {
            LogMessages.LOGGER.attemptingToRegisterUnassignableContract(component.getClass().getName());
            return this;
         }
      }
      registerProviderInstance(component, contracts, null, false);
      return this;
   }

   @Override
   public Configuration getConfiguration()
   {
      return this;
   }

   @Override
   public RuntimeType getRuntimeType()
   {
      return RuntimeType.SERVER;
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      return getProperties().keySet();
   }

   @Override
   public boolean isEnabled(Feature feature)
   {
      return getEnabledFeatures().contains(feature);
   }

   @Override
   public boolean isEnabled(Class<? extends Feature> featureClass)
   {
      Collection<Feature> enabled = getEnabledFeatures();
      //logger.info("isEnabled(Class): " + featureClass.getName() + " # enabled: " + enabled.size());
      if (enabled == null) return false;
      for (Feature feature : enabled)
      {
         //logger.info("  looking at: " + feature.getClass());
         if (featureClass.equals(feature.getClass()))
         {
            //logger.info("   found: " + featureClass.getName());
            return true;
         }
      }
      //logger.info("not enabled class: " + featureClass.getName());
      return false;
   }

   @Override
   public boolean isRegistered(Object component)
   {
      return getProviderInstances().contains(component);
   }

   @Override
   public boolean isRegistered(Class<?> componentClass)
   {
      return getClassContracts().containsKey(componentClass);
   }

   @Override
   public Map<Class<?>, Integer> getContracts(Class<?> componentClass)
   {
      if (classContracts == null && parent == null) return Collections.emptyMap();
      else if (classContracts == null) return parent.getContracts(componentClass);
      else
      {
         Map<Class<?>, Integer> classIntegerMap = classContracts.get(componentClass);
         if (classIntegerMap == null) return Collections.emptyMap();
         return classIntegerMap;
      }
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      Set<Class<?>> providerClasses = getProviderClasses();
      return (providerClasses == null || providerClasses.isEmpty())
            ? Collections.emptySet()
            : Collections.unmodifiableSet(providerClasses);
   }

   @Override
   public Set<Object> getInstances()
   {
      Set<Object> providerInstances = getProviderInstances();
      return (providerInstances == null || providerInstances.isEmpty())
            ? Collections.emptySet()
            : Collections.unmodifiableSet(providerInstances);
   }

   @Override
   public Link.Builder createLinkBuilder()
   {
      return new LinkBuilderImpl();
   }

   public <I extends RxInvoker> RxInvokerProvider<I> getRxInvokerProvider(Class<I> clazz) {
      for (Entry<Class<?>, Map<Class<?>, Integer>> entry : classContracts.entrySet()) {
         if (entry.getValue().containsKey(RxInvokerProvider.class)) {
            RxInvokerProvider<?> rip = (RxInvokerProvider<?>)createProviderInstance(entry.getKey());
            if (rip.isProviderFor(clazz)) {
               return (RxInvokerProvider<I>)rip;
            }
         }
      }
      return null;
   }
   
   public RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz) {
      Class<? extends RxInvokerProvider> rxInvokerProviderClass = reactiveClasses.get(clazz);
      if (rxInvokerProviderClass != null)
      {
         return createProviderInstance(rxInvokerProviderClass);
      }
      return null;
   }

   public boolean isReactive(Class<?> clazz)
   {
      return reactiveClasses.keySet().contains(clazz);
   }

   private void addResourceClassProcessor(Class<ResourceClassProcessor> processorClass, int priority)
   {
      ResourceClassProcessor processor = createProviderInstance(processorClass);
      addResourceClassProcessor(processor, priority);
   }

   private void addResourceClassProcessor(ResourceClassProcessor processor, int priority)
   {
      resourceBuilder.registerResourceClassProcessor(processor, priority);
   }

   public ResourceBuilder getResourceBuilder() {
      return resourceBuilder;
   }


}
