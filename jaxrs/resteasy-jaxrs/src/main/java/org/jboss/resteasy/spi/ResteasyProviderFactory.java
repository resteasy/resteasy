package org.jboss.resteasy.spi;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.EncoderPrecedence;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.RedirectPrecedence;
import org.jboss.resteasy.annotations.interception.SecurityPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.core.interception.ContainerRequestFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.LegacyPrecedence;
import org.jboss.resteasy.core.interception.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.WriterInterceptorRegistry;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.EntityTagDelegate;
import org.jboss.resteasy.plugins.delegates.LinkDelegate;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.UriHeaderDelegate;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.specimpl.VariantListBuilderImpl;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.PickConstructor;
import org.jboss.resteasy.util.ThreadLocalStack;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
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
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ResteasyProviderFactory extends RuntimeDelegate implements Providers
{
   /**
    * Allow us to sort message body implementations that are more specific for their types
    * i.e. MessageBodyWriter<Object> is less specific than MessageBodyWriter<String>.
    * <p/>
    * This helps out a lot when the desired media type is a wildcard and to weed out all the possible
    * default mappings.
    */
   protected static class SortedKey<T> implements Comparable<SortedKey<T>>, MediaTypeMap.Typed
   {
      public Class readerClass;
      public T obj;

      public boolean isGeneric = false;

      public boolean isBuiltin = false;

      public Class template = null;


      private SortedKey(Class intf, T reader, Class readerClass, boolean isBuiltin)
      {
         this(intf, reader, readerClass);
         this.isBuiltin = isBuiltin;
      }


      private SortedKey(Class intf, T reader, Class readerClass)
      {
         this.readerClass = readerClass;
         this.obj = reader;
         // check the super class for the generic type 1st
         template = Types.getTemplateParameterOfInterface(readerClass, intf);
         isGeneric = template == null || Object.class.equals(template);
      }

      public int compareTo(SortedKey<T> tMessageBodyKey)
      {
         // Sort more specific template parameter types before non-specific
         // Sort user provider before builtins
         if (this == tMessageBodyKey) return 0;
         if (isGeneric != tMessageBodyKey.isGeneric)
         {
            if (isGeneric) return 1;
            else return -1;
         }
         if (isBuiltin == tMessageBodyKey.isBuiltin) return 0;
         if (isBuiltin) return 1;
         else return -1;
      }

      public Class getType()
      {
         return template;
      }
   }

   protected static AtomicReference<ResteasyProviderFactory> pfr = new AtomicReference<ResteasyProviderFactory>();
   protected static ThreadLocalStack<Map<Class<?>, Object>> contextualData = new ThreadLocalStack<Map<Class<?>, Object>>();
   protected static int maxForwards = 20;
   protected static volatile ResteasyProviderFactory instance;
   public static boolean registerBuiltinByDefault = true;

   protected MediaTypeMap<SortedKey<MessageBodyReader>> messageBodyReaders;
   protected MediaTypeMap<SortedKey<MessageBodyWriter>> messageBodyWriters;
   protected Map<Class<?>, ExceptionMapper> exceptionMappers;
   protected Map<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> contextResolvers;
   protected Map<Class<?>, StringConverter> stringConverters;
   protected Map<Class<?>, Class<? extends StringParameterUnmarshaller>> stringParameterUnmarshallers;
   protected Set<Class<?>> providerClasses;
   protected Set<Object> providerInstances;

   protected Map<Class<?>, HeaderDelegate> headerDelegates;

   protected LegacyPrecedence precedence;
   protected ReaderInterceptorRegistry serverReaderInterceptorRegistry;
   protected WriterInterceptorRegistry serverWriterInterceptorRegistry;
   protected ContainerRequestFilterRegistry containerRequestFilterRegistry;
   protected ContainerResponseFilterRegistry containerResponseFilterRegistry;

   protected JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilters;
   protected JaxrsInterceptorRegistry<ClientResponseFilter> clientResponseFilters;
   protected ReaderInterceptorRegistry clientReaderInterceptorRegistry;
   protected WriterInterceptorRegistry clientWriterInterceptorRegistry;
   protected InterceptorRegistry<ClientExecutionInterceptor> clientExecutionInterceptorRegistry;

   protected List<ClientErrorInterceptor> clientErrorInterceptors;

   protected boolean builtinsRegistered = false;
   protected boolean registerBuiltins = true;

   protected InjectorFactory injectorFactory;
   protected ResteasyProviderFactory parent;

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
    * @param parent
    */
   public ResteasyProviderFactory(ResteasyProviderFactory parent)
   {
      this.parent = parent;
   }

   protected void initialize()
   {
      providerClasses = new HashSet<Class<?>>();
      providerInstances = new HashSet<Object>();
      messageBodyReaders = new MediaTypeMap<SortedKey<MessageBodyReader>>();
      messageBodyWriters = new MediaTypeMap<SortedKey<MessageBodyWriter>>();
      exceptionMappers = new HashMap<Class<?>, ExceptionMapper>();
      contextResolvers = new HashMap<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>>();
      stringConverters = new HashMap<Class<?>, StringConverter>();
      stringParameterUnmarshallers = new HashMap<Class<?>, Class<? extends StringParameterUnmarshaller>>();

      headerDelegates = new HashMap<Class<?>, HeaderDelegate>();

      precedence = new LegacyPrecedence();
      serverReaderInterceptorRegistry = new ReaderInterceptorRegistry(this, precedence);
      serverWriterInterceptorRegistry = new WriterInterceptorRegistry(this, precedence);
      containerRequestFilterRegistry = new ContainerRequestFilterRegistry(this, precedence);
      containerResponseFilterRegistry = new ContainerResponseFilterRegistry(this, precedence);

      clientRequestFilters = new JaxrsInterceptorRegistry<ClientRequestFilter>(this, ClientRequestFilter.class);
      clientResponseFilters = new JaxrsInterceptorRegistry<ClientResponseFilter>(this, ClientResponseFilter.class);
      clientReaderInterceptorRegistry = new ReaderInterceptorRegistry(this, precedence);
      clientWriterInterceptorRegistry = new WriterInterceptorRegistry(this, precedence);
      clientExecutionInterceptorRegistry = new InterceptorRegistry<ClientExecutionInterceptor>(ClientExecutionInterceptor.class, this);

      clientErrorInterceptors = new ArrayList<ClientErrorInterceptor>();

      builtinsRegistered = false;
      registerBuiltins = true;

      injectorFactory = new InjectorFactoryImpl(this);
      registerDefaultInterceptorPrecedences();
      addHeaderDelegate(MediaType.class, new MediaTypeHeaderDelegate());
      addHeaderDelegate(NewCookie.class, new NewCookieHeaderDelegate());
      addHeaderDelegate(Cookie.class, new CookieHeaderDelegate());
      addHeaderDelegate(URI.class, new UriHeaderDelegate());
      addHeaderDelegate(EntityTag.class, new EntityTagDelegate());
      addHeaderDelegate(CacheControl.class, new CacheControlDelegate());
      addHeaderDelegate(Locale.class, new LocaleDelegate());
      addHeaderDelegate(LinkHeader.class, new LinkHeaderDelegate());
      addHeaderDelegate(javax.ws.rs.core.Link.class, new LinkDelegate());
   }

   protected MediaTypeMap<SortedKey<MessageBodyReader>> getMessageBodyReaders()
   {
      if (messageBodyReaders == null && parent != null) return parent.getMessageBodyReaders();
      return messageBodyReaders;
   }

   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getMessageBodyWriters()
   {
      if (messageBodyWriters == null && parent != null) return parent.getMessageBodyWriters();
      return messageBodyWriters;
   }

   protected Map<Class<?>, ExceptionMapper> getExceptionMappers()
   {
      if (exceptionMappers == null && parent != null) return parent.getExceptionMappers();
      return exceptionMappers;
   }

   protected Map<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> getContextResolvers()
   {
      if (contextResolvers == null && parent != null) return parent.getContextResolvers();
      return contextResolvers;
   }

   protected Map<Class<?>, StringConverter> getStringConverters()
   {
      if (stringConverters == null && parent != null) return parent.getStringConverters();
      return stringConverters;
   }

   protected Map<Class<?>, Class<? extends StringParameterUnmarshaller>> getStringParameterUnmarshallers()
   {
      if (stringParameterUnmarshallers == null && parent != null) return parent.getStringParameterUnmarshallers();
      return stringParameterUnmarshallers;
   }

   /**
    * Copy
    *
    * @return
    */
   public Set<Class<?>> getProviderClasses()
   {
      if (providerClasses == null && parent != null) return parent.getProviderClasses();
      Set<Class<?>> set = new HashSet<Class<?>>();
      if (parent != null) set.addAll(parent.getProviderClasses());
      set.addAll(providerClasses);
      return providerClasses;
   }

   /**
    * Copy
    *
    * @return
    */
   public Set<Object> getProviderInstances()
   {
      if (providerInstances == null && parent != null) return parent.getProviderInstances();
      Set<Object> set = new HashSet<Object>();
      if (parent != null) set.addAll(parent.getProviderInstances());
      set.addAll(providerInstances);
      return providerInstances;
   }

   protected LegacyPrecedence getPrecedence()
   {
      if (precedence == null && parent != null) return parent.getPrecedence();
      return precedence;
   }

   public ResteasyProviderFactory getParent()
   {
      return parent;
   }

   protected void copyParent()
   {
      providerClasses = new HashSet<Class<?>>();
      providerClasses.addAll(parent.getProviderClasses());

      providerInstances = new HashSet<Object>();
      providerInstances.addAll(parent.getProviderInstances());

      messageBodyReaders = parent.getMessageBodyReaders().clone();
      messageBodyWriters = parent.getMessageBodyWriters().clone();

      exceptionMappers = new HashMap<Class<?>, ExceptionMapper>();
      exceptionMappers.putAll(parent.getExceptionMappers());

      contextResolvers = new HashMap<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>>();
      for (Map.Entry<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> entry : parent.getContextResolvers().entrySet())
      {
         contextResolvers.put(entry.getKey(), entry.getValue().clone());
      }

      stringConverters = new HashMap<Class<?>, StringConverter>();
      stringConverters.putAll(parent.getStringConverters());

      stringParameterUnmarshallers = new HashMap<Class<?>, Class<? extends StringParameterUnmarshaller>>();
      stringParameterUnmarshallers.putAll(parent.getStringParameterUnmarshallers());

      headerDelegates = new HashMap<Class<?>, HeaderDelegate>();
      headerDelegates.putAll(parent.getHeaderDelegates());

      precedence = parent.getPrecedence().clone();
      serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
      serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
      containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(this);
      containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(this);

      clientRequestFilters = parent.getClientRequestFilters().clone(this);
      clientResponseFilters = parent.getClientResponseFilters().clone(this);
      clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
      clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
      clientExecutionInterceptorRegistry = parent.getClientExecutionInterceptorRegistry().cloneTo(this);

      clientErrorInterceptors = new ArrayList<ClientErrorInterceptor>();
      clientErrorInterceptors.addAll(parent.getClientErrorInterceptors());

      builtinsRegistered = false;
      registerBuiltins = true;

      injectorFactory = parent.getInjectorFactory();
   }

   protected void registerDefaultInterceptorPrecedences(InterceptorRegistry registry)
   {
      // legacy
      registry.appendPrecedence(SecurityPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(HeaderDecoratorPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(EncoderPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(RedirectPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(DecoderPrecedence.PRECEDENCE_STRING);

   }

   protected void registerDefaultInterceptorPrecedences()
   {
      precedence.addPrecedence(SecurityPrecedence.PRECEDENCE_STRING, BindingPriority.SECURITY);
      precedence.addPrecedence(HeaderDecoratorPrecedence.PRECEDENCE_STRING, BindingPriority.HEADER_DECORATOR);
      precedence.addPrecedence(EncoderPrecedence.PRECEDENCE_STRING, BindingPriority.ENCODER);
      precedence.addPrecedence(RedirectPrecedence.PRECEDENCE_STRING, BindingPriority.ENCODER + 50);
      precedence.addPrecedence(DecoderPrecedence.PRECEDENCE_STRING, BindingPriority.DECODER);

      registerDefaultInterceptorPrecedences(getClientExecutionInterceptorRegistry());
   }

   /**
    * Append interceptor predence
    *
    * @param precedence
    */
   public void appendInterceptorPrecedence(String precedence)
   {
      if (this.precedence == null)
      {
         this.precedence = parent.getPrecedence().clone();
      }
      if (clientExecutionInterceptorRegistry == null)
      {
         clientExecutionInterceptorRegistry = parent.getClientExecutionInterceptorRegistry().cloneTo(this);
      }
      this.precedence.appendPrecedence(precedence);
      clientExecutionInterceptorRegistry.appendPrecedence(precedence);
   }

   /**
    * @param after         put newPrecedence after this
    * @param newPrecedence
    */
   public void insertInterceptorPrecedenceAfter(String after, String newPrecedence)
   {
      if (this.precedence == null)
      {
         this.precedence = parent.getPrecedence().clone();
      }
      if (clientExecutionInterceptorRegistry == null)
      {
         clientExecutionInterceptorRegistry = parent.getClientExecutionInterceptorRegistry().cloneTo(this);
      }
      this.precedence.insertPrecedenceAfter(after, newPrecedence);

      getClientExecutionInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
   }

   /**
    * @param before        put newPrecedence before this
    * @param newPrecedence
    */
   public void insertInterceptorPrecedenceBefore(String before, String newPrecedence)
   {
      if (this.precedence == null)
      {
         this.precedence = parent.getPrecedence().clone();
      }
      if (clientExecutionInterceptorRegistry == null)
      {
         clientExecutionInterceptorRegistry = parent.getClientExecutionInterceptorRegistry().cloneTo(this);
      }
      this.precedence.insertPrecedenceBefore(before, newPrecedence);

      getClientExecutionInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
   }


   public static <T> void pushContext(Class<T> type, T data)
   {
      getContextDataMap().put(type, data);
   }

   public static void pushContextDataMap(Map<Class<?>, Object> map)
   {
      contextualData.setLast(map);
   }

   public static Map<Class<?>, Object> getContextDataMap()
   {
      return getContextDataMap(true);
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) getContextDataMap().get(type);
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
         throw new BadRequestException(
                 "You have exceeded your maximum forwards ResteasyProviderFactory allows.  Last good uri: "
                         + getContextData(UriInfo.class).getPath());
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
    * Will not initialize singleton if not set
    *
    * @return
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
      instance = factory;
      RuntimeDelegate.setInstance(factory);
   }

   /**
    * Initializes ResteasyProviderFactory singleton if not set
    *
    * @return
    */
   public static ResteasyProviderFactory getInstance()
   {
      instance = (ResteasyProviderFactory) RuntimeDelegate.getInstance();
      if (registerBuiltinByDefault) RegisterBuiltin.register(instance);
      return instance;
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

   public InterceptorRegistry<ClientExecutionInterceptor> getClientExecutionInterceptorRegistry()
   {
      if (clientExecutionInterceptorRegistry == null && parent != null) return parent.getClientExecutionInterceptorRegistry();
      return clientExecutionInterceptorRegistry;
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

   public JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilters()
   {
      if (clientRequestFilters == null && parent != null) return parent.getClientRequestFilters();
      return clientRequestFilters;
   }

   public JaxrsInterceptorRegistry<ClientResponseFilter> getClientResponseFilters()
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
      return new UriBuilderImpl();
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
      if (headerDelegates == null && parent != null) return parent.createHeaderDelegate(tClass);
      return headerDelegates.get(tClass);
   }

   protected Map<Class<?>, HeaderDelegate> getHeaderDelegates()
   {
      if (headerDelegates == null && parent != null) return parent.getHeaderDelegates();
      return headerDelegates;
   }

   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      if (headerDelegates == null)
      {
         headerDelegates = new HashMap<Class<?>, HeaderDelegate>();
         headerDelegates.putAll(parent.getHeaderDelegates());
      }
      headerDelegates.put(clazz, header);
   }

   protected void addMessageBodyReader(Class<? extends MessageBodyReader> provider, boolean isBuiltin)
   {
      MessageBodyReader reader = createProviderInstance(provider);
      addMessageBodyReader(reader, provider, isBuiltin);
   }

   protected void addMessageBodyReader(MessageBodyReader provider)
   {
      addMessageBodyReader(provider, false);
   }

   protected void addMessageBodyReader(MessageBodyReader provider, boolean isBuiltin)
   {
      addMessageBodyReader(provider, provider.getClass(), isBuiltin);
   }

   /**
    * Specify the provider class.  This is there jsut in case the provider instance is a proxy.  Proxies tend
    * to lose generic type information
    *
    * @param provider
    * @param providerClass
    * @param isBuiltin
    */

   protected void addMessageBodyReader(MessageBodyReader provider, Class providerClass, boolean isBuiltin)
   {
      SortedKey<MessageBodyReader> key = new SortedKey<MessageBodyReader>(MessageBodyReader.class, provider, providerClass, isBuiltin);
      injectProperties(provider);
      Consumes consumeMime = provider.getClass().getAnnotation(Consumes.class);
      if (messageBodyReaders == null)
      {
         messageBodyReaders = parent.getMessageBodyReaders().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.valueOf(consume);
            messageBodyReaders.add(mime, key);
         }
      }
      else
      {
         messageBodyReaders.add(new MediaType("*", "*"), key);
      }
   }

   protected void addMessageBodyWriter(Class<? extends MessageBodyWriter> provider, boolean isBuiltin)
   {
      MessageBodyWriter writer = createProviderInstance(provider);
      addMessageBodyWriter(writer, provider, isBuiltin);
   }

   protected void addMessageBodyWriter(MessageBodyWriter provider)
   {
      addMessageBodyWriter(provider, provider.getClass(), false);
   }

   /**
    * Specify the provider class.  This is there jsut in case the provider instance is a proxy.  Proxies tend
    * to lose generic type information
    *
    * @param provider
    * @param providerClass
    * @param isBuiltin
    */
   protected void addMessageBodyWriter(MessageBodyWriter provider, Class providerClass, boolean isBuiltin)
   {
      injectProperties(provider);
      Produces consumeMime = provider.getClass().getAnnotation(Produces.class);
      SortedKey<MessageBodyWriter> key = new SortedKey<MessageBodyWriter>(MessageBodyWriter.class, provider, providerClass, isBuiltin);
      if (messageBodyWriters == null)
      {
         messageBodyWriters = parent.getMessageBodyWriters().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.valueOf(consume);
            messageBodyWriters.add(mime, key);
         }
      }
      else
      {
         messageBodyWriters.add(new MediaType("*", "*"), key);
      }
   }

   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      List<SortedKey<MessageBodyReader>> readers = getMessageBodyReaders().getPossible(mediaType, type);

      for (SortedKey<MessageBodyReader> reader : readers)
      {
         if (reader.obj.isReadable(type, genericType, annotations, mediaType))
         {
            return (MessageBodyReader<T>) reader.obj;
         }
      }
      return null;
   }

   protected void addExceptionMapper(Class<? extends ExceptionMapper> providerClass)
   {
      ExceptionMapper provider = createProviderInstance(providerClass);
      addExceptionMapper(provider, providerClass);
   }

   protected void addExceptionMapper(ExceptionMapper provider)
   {
      addExceptionMapper(provider, provider.getClass());
   }

   protected void addExceptionMapper(ExceptionMapper provider, Class providerClass)
   {
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(providerClass, ExceptionMapper.class)[0];
      addExceptionMapper(provider, exceptionType);
   }


   protected void addExceptionMapper(ExceptionMapper provider, Type exceptionType)
   {
      injectProperties(provider);

      Class<?> exceptionClass = Types.getRawType(exceptionType);
      if (!Throwable.class.isAssignableFrom(exceptionClass))
      {
         throw new RuntimeException("Incorrect type parameter. ExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.");
      }
      if (exceptionMappers == null)
      {
         exceptionMappers = new HashMap<Class<?>, ExceptionMapper>();
         exceptionMappers.putAll(parent.getExceptionMappers());
      }
      exceptionMappers.put(exceptionClass, provider);
   }

   /**
    * Add a {@link ClientErrorInterceptor} to this provider factory instance.
    * Duplicate handlers are ignored. (For Client Proxy API only)
    */
   public void addClientErrorInterceptor(ClientErrorInterceptor handler)
   {
      if (clientErrorInterceptors == null)
      {
         clientErrorInterceptors = new ArrayList<ClientErrorInterceptor>();
         clientErrorInterceptors.addAll(parent.getClientErrorInterceptors());
      }
      if (!clientErrorInterceptors.contains(handler))
      {
         clientErrorInterceptors.add(handler);
      }
   }


   /**
    * Return the list of currently registered {@link ClientErrorInterceptor} instances.
    */
   public List<ClientErrorInterceptor> getClientErrorInterceptors()
   {
      if (clientErrorInterceptors == null && parent != null) return parent.getClientErrorInterceptors();
      return clientErrorInterceptors;
   }

   protected void addContextResolver(Class<? extends ContextResolver> resolver, boolean builtin)
   {
      ContextResolver writer = createProviderInstance(resolver);
      addContextResolver(writer, resolver, builtin);
   }

   protected void addContextResolver(ContextResolver provider)
   {
      addContextResolver(provider, false);
   }
   protected void addContextResolver(ContextResolver provider, boolean builtin)
   {
      addContextResolver(provider, provider.getClass(), builtin);
   }

   protected void addContextResolver(ContextResolver provider, Class providerClass, boolean builtin)
   {
      Type parameter = Types.getActualTypeArgumentsOfAnInterface(providerClass, ContextResolver.class)[0];
      addContextResolver(provider, parameter, providerClass, builtin);
   }

   protected void addContextResolver(ContextResolver provider, Type typeParameter, Class providerClass, boolean builtin)
   {
      injectProperties(provider);
      Class<?> parameterClass = Types.getRawType(typeParameter);
      if (contextResolvers == null)
      {
         contextResolvers = new HashMap<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>>();
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
      SortedKey<ContextResolver> key = new SortedKey<ContextResolver>(ContextResolver.class, provider, providerClass, builtin);
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

   public void injectProperties(Object o)
   {
      injectorFactory.createPropertyInjector(o.getClass()).inject(o);
   }

   protected void addStringConverter(Class<? extends StringConverter> resolver)
   {
      StringConverter writer = createProviderInstance(resolver);
      addStringConverter(writer, resolver);
   }

   protected void addStringConverter(StringConverter provider)
   {
      addStringConverter(provider, provider.getClass());
   }

   protected void addStringConverter(StringConverter provider, Class providerClass)
   {
      Type parameter = Types.getActualTypeArgumentsOfAnInterface(providerClass, StringConverter.class)[0];
      addStringConverter(provider, parameter);
   }

   protected void addStringConverter(StringConverter provider, Type typeParameter)
   {
      injectProperties(provider);
      Class<?> parameterClass = Types.getRawType(typeParameter);
      if (stringConverters == null)
      {
         stringConverters = new HashMap<Class<?>, StringConverter>();
         stringConverters.putAll(parent.getStringConverters());
      }
      stringConverters.put(parameterClass, provider);
   }


   public void addStringParameterUnmarshaller(Class<? extends StringParameterUnmarshaller> provider)
   {
      if (stringParameterUnmarshallers == null)
      {
         stringParameterUnmarshallers = new HashMap<Class<?>, Class<? extends StringParameterUnmarshaller>>();
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

   public List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type)
   {
      MediaTypeMap<SortedKey<ContextResolver>> resolvers = getContextResolvers().get(clazz);
      if (resolvers == null) return null;
      List<ContextResolver> rtn = new ArrayList<ContextResolver>();

      List<SortedKey<ContextResolver>> list = resolvers.getPossible(type);
      for (SortedKey<ContextResolver> resolver : list)
      {
         rtn.add(resolver.obj);
      }
      return rtn;
   }

   public StringConverter getStringConverter(Class<?> clazz)
   {
      if (getStringConverters().size() == 0) return null;
      return getStringConverters().get(clazz);
   }

   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      if (getStringParameterUnmarshallers().size() == 0) return null;
      Class<? extends StringParameterUnmarshaller> un = getStringParameterUnmarshallers().get(clazz);
      if (un == null) return null;
      StringParameterUnmarshaller<T> provider = createProviderInstance(un);
      injectProperties(provider);
      return provider;

   }

   public void registerProvider(Class provider)
   {
      registerProvider(provider, false);
   }

   /**
    * Convert an object to a string.  First try StringConverter then, object.ToString()
    *
    * @param object
    * @return
    */
   public String toString(Object object)
   {
      if (object instanceof String)
         return (String) object;
      StringConverter converter = getStringConverter(object
              .getClass());
      if (converter != null)
         return converter.toString(object);
      else
         return object.toString();

   }

   /**
    * Convert an object to a header string.  First try StringConverter, then HeaderDelegate, then object.toString()
    *
    * @param object
    * @return
    */
   public String toHeaderString(Object object)
   {
      if (object instanceof String) return (String)object;
      StringConverter converter = getStringConverter(object
              .getClass());
      if (converter != null)
         return converter.toString(object);

      RuntimeDelegate.HeaderDelegate delegate = createHeaderDelegate(object.getClass());
      if (delegate != null)
         return delegate.toString(object);
      else
         return object.toString();

   }

   /**
    * Register a @Provider class.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider
    */
   public void registerProvider(Class provider, boolean isBuiltin)
   {
      if (MessageBodyReader.class.isAssignableFrom(provider))
      {
         try
         {
            addMessageBodyReader(provider, isBuiltin);
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
            addMessageBodyWriter(provider, isBuiltin);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate MessageBodyWriter", e);
         }
      }
      if (ExceptionMapper.class.isAssignableFrom(provider))
      {
         try
         {
            addExceptionMapper(provider);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate ExceptionMapper", e);
         }
      }
      if (ClientExecutionInterceptor.class.isAssignableFrom(provider))
      {
         if (clientExecutionInterceptorRegistry == null)
         {
            clientExecutionInterceptorRegistry = parent.getClientExecutionInterceptorRegistry().cloneTo(this);
         }
         clientExecutionInterceptorRegistry.register(provider);
      }
      if (PreProcessInterceptor.class.isAssignableFrom(provider))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(this);
         }
         containerRequestFilterRegistry.registerLegacy(provider);
      }
      if (PostProcessInterceptor.class.isAssignableFrom(provider))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(this);
         }
         containerResponseFilterRegistry.registerLegacy(provider);
      }
      if (ReaderInterceptor.class.isAssignableFrom(provider))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerClass(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerClass(provider);
         }
         if (!provider.isAnnotationPresent(ServerInterceptor.class) && !provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerClass(provider);
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerClass(provider);
         }
      }
      if (WriterInterceptor.class.isAssignableFrom(provider))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerClass(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerClass(provider);
         }
         if (!provider.isAnnotationPresent(ServerInterceptor.class) && !provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerClass(provider);
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerClass(provider);
         }
      }
      if (MessageBodyWriterInterceptor.class.isAssignableFrom(provider))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerLegacy(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerLegacy(provider);
         }
         if (!provider.isAnnotationPresent(ServerInterceptor.class) && !provider.isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException("Interceptor class must be annotated with @ServerInterceptor and/or @ClientInterceptor");
         }

      }
      if (MessageBodyReaderInterceptor.class.isAssignableFrom(provider))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerLegacy(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerLegacy(provider);
         }
         if (!provider.isAnnotationPresent(ServerInterceptor.class) && !provider.isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException("Interceptor class must be annotated with @ServerInterceptor and/or @ClientInterceptor");
         }

      }
      if (ContextResolver.class.isAssignableFrom(provider))
      {
         try
         {
            addContextResolver(provider, true);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate ContextResolver", e);
         }
      }
      if (StringConverter.class.isAssignableFrom(provider))
      {
         addStringConverter(provider);
      }
      if (StringParameterUnmarshaller.class.isAssignableFrom(provider))
      {
         addStringParameterUnmarshaller(provider);
      }
      if (InjectorFactory.class.isAssignableFrom(provider))
      {
         try
         {
            Constructor constructor = provider.getConstructor(ResteasyProviderFactory.class);
            this.injectorFactory = (InjectorFactory) constructor.newInstance(this);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      providerClasses.add(provider);
   }

   /**
    * Register a @Provider object.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider
    */
   public void registerProviderInstance(Object provider)
   {
      if (provider instanceof MessageBodyReader)
      {
         try
         {
            addMessageBodyReader((MessageBodyReader) provider);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate MessageBodyReader", e);
         }
      }
      if (provider instanceof MessageBodyWriter)
      {
         try
         {
            addMessageBodyWriter((MessageBodyWriter) provider);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate MessageBodyWriter", e);
         }
      }
      if (provider instanceof ExceptionMapper)
      {
         try
         {
            addExceptionMapper((ExceptionMapper) provider);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate ExceptionMapper", e);
         }
      }
      if (provider instanceof ContextResolver)
      {
         try
         {
            addContextResolver((ContextResolver) provider);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Unable to instantiate ContextResolver", e);
         }
      }
      if (provider instanceof ClientExecutionInterceptor)
      {
         if (clientExecutionInterceptorRegistry == null)
         {
            clientExecutionInterceptorRegistry = parent.getClientExecutionInterceptorRegistry().cloneTo(this);
         }
         clientExecutionInterceptorRegistry.register((ClientExecutionInterceptor) provider);
      }
      if (provider instanceof PreProcessInterceptor)
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(this);
         }
         containerRequestFilterRegistry.registerLegacy((PreProcessInterceptor) provider);
      }
      if (provider instanceof PostProcessInterceptor)
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(this);
         }
         containerResponseFilterRegistry.registerLegacy((PostProcessInterceptor) provider);
      }
      if (provider instanceof ReaderInterceptor)
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider);
         }
         if (!provider.getClass().isAnnotationPresent(ServerInterceptor.class) && !provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider);
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider);
         }
      }
      if (provider instanceof WriterInterceptor)
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider);
         }
         if (!provider.getClass().isAnnotationPresent(ServerInterceptor.class) && !provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider);
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider);
         }
      }
      if (provider instanceof MessageBodyWriterInterceptor)
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(this);
            }
            serverWriterInterceptorRegistry.registerLegacy((MessageBodyWriterInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(this);
            }
            clientWriterInterceptorRegistry.registerLegacy((MessageBodyWriterInterceptor) provider);
         }
         if (!provider.getClass().isAnnotationPresent(ServerInterceptor.class) && !provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException("Interceptor class " + provider.getClass() + " must be annotated with @ServerInterceptor and/or @ClientInterceptor");
         }

      }
      if (provider instanceof MessageBodyReaderInterceptor)
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(this);
            }
            serverReaderInterceptorRegistry.registerLegacy((MessageBodyReaderInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(this);
            }
            clientReaderInterceptorRegistry.registerLegacy((MessageBodyReaderInterceptor) provider);
         }
         if (!provider.getClass().isAnnotationPresent(ServerInterceptor.class) && !provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException("Interceptor class " + provider.getClass() + " must be annotated with @ServerInterceptor and/or @ClientInterceptor");
         }

      }
      if (provider instanceof StringConverter)
      {
         addStringConverter((StringConverter) provider);
      }
      if (provider instanceof InjectorFactory)
      {
         this.injectorFactory = (InjectorFactory) provider;
      }
      providerInstances.add(provider);
   }

   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return getExceptionMappers().get(type);
   }

   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      List<SortedKey<MessageBodyWriter>> writers = getMessageBodyWriters().getPossible(mediaType, type);
      for (SortedKey<MessageBodyWriter> writer : writers)
      {
         //System.out.println("matching: " + writer.obj.getClass());
         if (writer.obj.isWriteable(type, genericType, annotations, mediaType))
         {
            return (MessageBodyWriter<T>) writer.obj;
         }
      }
      return null;
   }


   /**
    * this is a spec method that is unsupported.  it is an optional method anyways.
    *
    * @param applicationConfig
    * @param endpointType
    * @return
    * @throws IllegalArgumentException
    * @throws UnsupportedOperationException
    */
   public <T> T createEndpoint(Application applicationConfig, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException
   {
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
    *
    * @param clazz
    * @param <T>
    * @return
    */
   public <T> T createProviderInstance(Class<? extends T> clazz)
   {
      Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
      if (constructor == null)
      {
         throw new RuntimeException("Unable to find a public constructor for provider class " + clazz.getName());
      }
      ConstructorInjector constructorInjector = injectorFactory.createConstructor(constructor);

      T provider = (T) constructorInjector.construct();
      return provider;
   }
}
