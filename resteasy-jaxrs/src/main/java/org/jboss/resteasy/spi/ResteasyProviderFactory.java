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
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.EntityTagDelegate;
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

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
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
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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


      private SortedKey(Class intf, T reader, boolean isBuiltin)
      {
         this(intf, reader);
         this.isBuiltin = isBuiltin;
      }


      private SortedKey(Class intf, T reader)
      {
         this.readerClass = reader.getClass();
         this.obj = reader;
         // check the super class for the generic type 1st
         template = Types.getTemplateParameterOfInterface(readerClass, intf);
         isGeneric = template == null || Object.class.equals(template);
         /*
         Type impl = readerClass.getGenericSuperclass();
         // if it's null or object, check the interfaces
         // TODO: we may need more refinement here.
         if (impl == null || impl == Object.class)
         {
            Type[] impls = readerClass.getGenericInterfaces();
            if (impls.length > 0)
            {
               impl = impls[0];
            }
         }

         if (impl != null && (impl instanceof ParameterizedType))
         {
            ParameterizedType param = (ParameterizedType) impl;
            if (param.getActualTypeArguments()[0].equals(Object.class)) isGeneric = true;
         }
         else
         {
            isGeneric = true;
         }
         */
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

   protected MediaTypeMap<SortedKey<MessageBodyReader>> messageBodyReaders = new MediaTypeMap<SortedKey<MessageBodyReader>>();
   protected MediaTypeMap<SortedKey<MessageBodyWriter>> messageBodyWriters = new MediaTypeMap<SortedKey<MessageBodyWriter>>();
   protected Map<Class<?>, ExceptionMapper> exceptionMappers = new HashMap<Class<?>, ExceptionMapper>();
   protected Map<Class<?>, Object> providers = new HashMap<Class<?>, Object>();
   protected Map<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> contextResolvers = new HashMap<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>>();
   protected Map<Class<?>, StringConverter> stringConverters = new HashMap<Class<?>, StringConverter>();
   protected Map<Class<?>, Class<? extends StringParameterUnmarshaller>> stringParameterUnmarshallers = new HashMap<Class<?>, Class<? extends StringParameterUnmarshaller>>();

   protected Map<Class<?>, HeaderDelegate> headerDelegates = new HashMap<Class<?>, HeaderDelegate>();

   protected static AtomicReference<ResteasyProviderFactory> pfr = new AtomicReference<ResteasyProviderFactory>();
   protected static ThreadLocalStack<Map<Class<?>, Object>> contextualData = new ThreadLocalStack<Map<Class<?>, Object>>();
   protected static int maxForwards = 20;
   protected static volatile ResteasyProviderFactory instance;

   public static boolean registerBuiltinByDefault = true;

   protected InterceptorRegistry<MessageBodyReaderInterceptor> serverMessageBodyReaderInterceptorRegistry = new InterceptorRegistry<MessageBodyReaderInterceptor>(MessageBodyReaderInterceptor.class, this);
   protected InterceptorRegistry<MessageBodyWriterInterceptor> serverMessageBodyWriterInterceptorRegistry = new InterceptorRegistry<MessageBodyWriterInterceptor>(MessageBodyWriterInterceptor.class, this);
   protected InterceptorRegistry<PreProcessInterceptor> serverPreProcessInterceptorRegistry = new InterceptorRegistry<PreProcessInterceptor>(PreProcessInterceptor.class, this);
   protected InterceptorRegistry<PostProcessInterceptor> serverPostProcessInterceptorRegistry = new InterceptorRegistry<PostProcessInterceptor>(PostProcessInterceptor.class, this);

   protected InterceptorRegistry<MessageBodyReaderInterceptor> clientMessageBodyReaderInterceptorRegistry = new InterceptorRegistry<MessageBodyReaderInterceptor>(MessageBodyReaderInterceptor.class, this);
   protected InterceptorRegistry<MessageBodyWriterInterceptor> clientMessageBodyWriterInterceptorRegistry = new InterceptorRegistry<MessageBodyWriterInterceptor>(MessageBodyWriterInterceptor.class, this);
   protected InterceptorRegistry<ClientExecutionInterceptor> clientExecutionInterceptorRegistry = new InterceptorRegistry<ClientExecutionInterceptor>(ClientExecutionInterceptor.class, this);

   protected List<ClientErrorInterceptor> clientErrorInterceptors = new ArrayList<ClientErrorInterceptor>();

   protected boolean builtinsRegistered = false;
   protected boolean registerBuiltins = true;

   protected InjectorFactory injectorFactory = new InjectorFactoryImpl(this);

   protected void registerDefaultInterceptorPrecedences(InterceptorRegistry registry)
   {
      registry.appendPrecedence(SecurityPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(HeaderDecoratorPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(EncoderPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(RedirectPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(DecoderPrecedence.PRECEDENCE_STRING);

   }

   protected void registerDefaultInterceptorPrecedences()
   {
      registerDefaultInterceptorPrecedences(getServerPreProcessInterceptorRegistry());
      registerDefaultInterceptorPrecedences(getServerMessageBodyReaderInterceptorRegistry());
      registerDefaultInterceptorPrecedences(getServerMessageBodyWriterInterceptorRegistry());
      registerDefaultInterceptorPrecedences(getServerPostProcessInterceptorRegistry());

      registerDefaultInterceptorPrecedences(getClientMessageBodyReaderInterceptorRegistry());
      registerDefaultInterceptorPrecedences(getClientMessageBodyWriterInterceptorRegistry());
      registerDefaultInterceptorPrecedences(getClientExecutionInterceptorRegistry());
   }

   /**
    * Append interceptor predence
    *
    * @param precedence
    */
   public void appendInterceptorPrecedence(String precedence)
   {
      getServerPreProcessInterceptorRegistry().appendPrecedence(precedence);
      getServerMessageBodyReaderInterceptorRegistry().appendPrecedence(precedence);
      getServerMessageBodyWriterInterceptorRegistry().appendPrecedence(precedence);
      getServerPostProcessInterceptorRegistry().appendPrecedence(precedence);

      getClientMessageBodyReaderInterceptorRegistry().appendPrecedence(precedence);
      getClientMessageBodyWriterInterceptorRegistry().appendPrecedence(precedence);
      getClientExecutionInterceptorRegistry().appendPrecedence(precedence);
   }

   /**
    * @param after         put newPrecedence after this
    * @param newPrecedence
    */
   public void insertInterceptorPrecedenceAfter(String after, String newPrecedence)
   {
      getServerPreProcessInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
      getServerMessageBodyReaderInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
      getServerMessageBodyWriterInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
      getServerPostProcessInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);

      getClientMessageBodyReaderInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
      getClientMessageBodyWriterInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
      getClientExecutionInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
   }

   /**
    * @param before        put newPrecedence before this
    * @param newPrecedence
    */
   public void insertInterceptorPrecedenceBefore(String before, String newPrecedence)
   {
      getServerPreProcessInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
      getServerMessageBodyReaderInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
      getServerMessageBodyWriterInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
      getServerPostProcessInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);

      getClientMessageBodyReaderInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
      getClientMessageBodyWriterInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
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

   public ResteasyProviderFactory()
   {
      // NOTE!!! It is important to put all initialization into initialize() as ThreadLocalResteasyProviderFactory
      // subclasses and delegates to this class.  
      initialize();
   }

   protected void initialize()
   {
      registerDefaultInterceptorPrecedences();
      addHeaderDelegate(MediaType.class, new MediaTypeHeaderDelegate());
      addHeaderDelegate(NewCookie.class, new NewCookieHeaderDelegate());
      addHeaderDelegate(Cookie.class, new CookieHeaderDelegate());
      addHeaderDelegate(URI.class, new UriHeaderDelegate());
      addHeaderDelegate(EntityTag.class, new EntityTagDelegate());
      addHeaderDelegate(CacheControl.class, new CacheControlDelegate());
      addHeaderDelegate(Locale.class, new LocaleDelegate());
      addHeaderDelegate(LinkHeader.class, new LinkHeaderDelegate());
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
      return injectorFactory;
   }

   public void setInjectorFactory(InjectorFactory injectorFactory)
   {
      this.injectorFactory = injectorFactory;
   }

   public InterceptorRegistry<MessageBodyReaderInterceptor> getServerMessageBodyReaderInterceptorRegistry()
   {
      return serverMessageBodyReaderInterceptorRegistry;
   }

   public InterceptorRegistry<MessageBodyWriterInterceptor> getServerMessageBodyWriterInterceptorRegistry()
   {
      return serverMessageBodyWriterInterceptorRegistry;
   }

   public InterceptorRegistry<PreProcessInterceptor> getServerPreProcessInterceptorRegistry()
   {
      return serverPreProcessInterceptorRegistry;
   }

   public InterceptorRegistry<PostProcessInterceptor> getServerPostProcessInterceptorRegistry()
   {
      return serverPostProcessInterceptorRegistry;
   }

   public InterceptorRegistry<MessageBodyReaderInterceptor> getClientMessageBodyReaderInterceptorRegistry()
   {
      return clientMessageBodyReaderInterceptorRegistry;
   }

   public InterceptorRegistry<MessageBodyWriterInterceptor> getClientMessageBodyWriterInterceptorRegistry()
   {
      return clientMessageBodyWriterInterceptorRegistry;
   }

   public InterceptorRegistry<ClientExecutionInterceptor> getClientExecutionInterceptorRegistry()
   {
      return clientExecutionInterceptorRegistry;
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
      return headerDelegates.get(tClass);
   }

   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      headerDelegates.put(clazz, header);
   }

   public void addMessageBodyReader(Class<? extends MessageBodyReader> provider)
   {
      addMessageBodyReader(provider, false);
   }

   public void addMessageBodyReader(Class<? extends MessageBodyReader> provider, boolean isBuiltin)
   {
      MessageBodyReader reader = getProviderInstance(provider);
      addMessageBodyReader(reader, isBuiltin);
   }

   public void addMessageBodyReader(MessageBodyReader provider)
   {
      addMessageBodyReader(provider, false);
   }

   public void addBuiltInMessageBodyReader(MessageBodyReader provider)
   {
      addMessageBodyReader(provider, true);
   }

   public void addMessageBodyReader(MessageBodyReader provider, boolean isBuiltin)
   {
      SortedKey<MessageBodyReader> key = new SortedKey<MessageBodyReader>(MessageBodyReader.class, provider, isBuiltin);
      injectProperties(provider);
      providers.put(provider.getClass(), provider);
      Consumes consumeMime = provider.getClass().getAnnotation(Consumes.class);
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

   public void addMessageBodyWriter(Class<? extends MessageBodyWriter> provider)
   {
      addMessageBodyWriter(provider, false);
   }

   public void addMessageBodyWriter(Class<? extends MessageBodyWriter> provider, boolean isBuiltin)
   {
      MessageBodyWriter writer = getProviderInstance(provider);
      addMessageBodyWriter(writer, isBuiltin);
   }

   public void addMessageBodyWriter(MessageBodyWriter provider)
   {
      addMessageBodyWriter(provider, false);
   }

   public void addBuiltInMessageBodyWriter(MessageBodyWriter provider)
   {
      addMessageBodyWriter(provider, true);
   }

   public void addMessageBodyWriter(MessageBodyWriter provider, boolean isBuiltin)
   {
      providers.put(provider.getClass(), provider);
      injectProperties(provider);
      Produces consumeMime = provider.getClass().getAnnotation(Produces.class);
      SortedKey<MessageBodyWriter> key = new SortedKey<MessageBodyWriter>(MessageBodyWriter.class, provider, isBuiltin);
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
      List<SortedKey<MessageBodyReader>> readers = messageBodyReaders.getPossible(mediaType, type);

      for (SortedKey<MessageBodyReader> reader : readers)
      {
         if (reader.obj.isReadable(type, genericType, annotations, mediaType))
         {
            return (MessageBodyReader<T>) reader.obj;
         }
      }
      return null;
   }

   public void addExceptionMapper(Class<? extends ExceptionMapper> providerClass)
   {
      ExceptionMapper provider = getProviderInstance(providerClass);
      addExceptionMapper(provider);
   }

   public void addExceptionMapper(ExceptionMapper provider)
   {
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(provider.getClass(), ExceptionMapper.class)[0];
      addExceptionMapper(provider, exceptionType);
   }

   public void addExceptionMapper(ExceptionMapper provider, Type exceptionType)
   {
      providers.put(provider.getClass(), provider);
      injectProperties(provider);

      Class<?> exceptionClass = Types.getRawType(exceptionType);
      if (!Throwable.class.isAssignableFrom(exceptionClass))
      {
         throw new RuntimeException("Incorrect type parameter. ExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.");
      }
      exceptionMappers.put(exceptionClass, provider);
   }

   /**
    * Add a {@link ClientErrorInterceptor} to this provider factory instance.
    * Duplicate handlers are ignored. (For Client Proxy API only)
    */
   public void addClientErrorInterceptor(ClientErrorInterceptor handler)
   {
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
      return clientErrorInterceptors;
   }

   public void addContextResolver(Class<? extends ContextResolver> resolver)
   {
      addContextResolver(resolver, false);
   }

   public void addContextResolver(Class<? extends ContextResolver> resolver, boolean builtin)
   {
      ContextResolver writer = getProviderInstance(resolver);
      addContextResolver(writer, builtin);
   }

   public void addContextResolver(ContextResolver provider)
   {
      addContextResolver(provider, false);
   }

   public void addContextResolver(ContextResolver provider, boolean builtin)
   {
      Type parameter = Types.getActualTypeArgumentsOfAnInterface(provider.getClass(), ContextResolver.class)[0];
      addContextResolver(provider, parameter, builtin);
   }

   public void addContextResolver(ContextResolver provider, Type typeParameter)
   {
      addContextResolver(provider, typeParameter, false);
   }

   public void addContextResolver(ContextResolver provider, Type typeParameter, boolean builtin)
   {
      providers.put(provider.getClass(), provider);
      injectProperties(provider);
      Class<?> parameterClass = Types.getRawType(typeParameter);
      MediaTypeMap<SortedKey<ContextResolver>> resolvers = contextResolvers.get(parameterClass);
      if (resolvers == null)
      {
         resolvers = new MediaTypeMap<SortedKey<ContextResolver>>();
         contextResolvers.put(parameterClass, resolvers);
      }
      Produces produces = provider.getClass().getAnnotation(Produces.class);
      SortedKey<ContextResolver> key = new SortedKey<ContextResolver>(provider.getClass(), provider, builtin);
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

   public void addStringConverter(Class<? extends StringConverter> resolver)
   {
      StringConverter writer = getProviderInstance(resolver);
      addStringConverter(writer);
   }

   public void addStringConverter(StringConverter provider)
   {
      Type parameter = Types.getActualTypeArgumentsOfAnInterface(provider.getClass(), StringConverter.class)[0];
      addStringConverter(provider, parameter);
   }

   public void addStringConverter(StringConverter provider, Type typeParameter)
   {
      providers.put(provider.getClass(), provider);
      injectProperties(provider);
      Class<?> parameterClass = Types.getRawType(typeParameter);
      stringConverters.put(parameterClass, provider);
   }


   public void addStringParameterUnmarshaller(Class<? extends StringParameterUnmarshaller> provider)
   {
      Type[] intfs = provider.getClass().getGenericInterfaces();
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
      MediaTypeMap<SortedKey<ContextResolver>> resolvers = contextResolvers.get(clazz);
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
      if (stringConverters.size() == 0) return null;
      return stringConverters.get(clazz);
   }

   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      if (stringParameterUnmarshallers.size() == 0) return null;
      Class<? extends StringParameterUnmarshaller> un = stringParameterUnmarshallers.get(clazz);
      StringParameterUnmarshaller<T> provider = getProviderInstance(un);
      injectProperties(provider);
      return provider;

   }

   public void registerProvider(Class provider)
   {
      registerProvider(provider, false);
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
         clientExecutionInterceptorRegistry.register(provider);
      }
      if (PreProcessInterceptor.class.isAssignableFrom(provider))
      {
         serverPreProcessInterceptorRegistry.register(provider);
      }
      if (PostProcessInterceptor.class.isAssignableFrom(provider))
      {
         serverPostProcessInterceptorRegistry.register(provider);
      }
      if (MessageBodyWriterInterceptor.class.isAssignableFrom(provider))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            serverMessageBodyWriterInterceptorRegistry.register(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            clientMessageBodyWriterInterceptorRegistry.register(provider);
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
            serverMessageBodyReaderInterceptorRegistry.register(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            clientMessageBodyReaderInterceptorRegistry.register(provider);
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
         clientExecutionInterceptorRegistry.register((ClientExecutionInterceptor) provider);
      }
      if (provider instanceof PreProcessInterceptor)
      {
         serverPreProcessInterceptorRegistry.register((PreProcessInterceptor) provider);
      }
      if (provider instanceof PostProcessInterceptor)
      {
         serverPostProcessInterceptorRegistry.register((PostProcessInterceptor) provider);
      }
      if (provider instanceof MessageBodyWriterInterceptor)
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            serverMessageBodyWriterInterceptorRegistry.register((MessageBodyWriterInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            clientMessageBodyWriterInterceptorRegistry.register((MessageBodyWriterInterceptor) provider);
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
            serverMessageBodyReaderInterceptorRegistry.register((MessageBodyReaderInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            clientMessageBodyReaderInterceptorRegistry.register((MessageBodyReaderInterceptor) provider);
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
   }

   /**
    * Obtain a registered @Provider instance keyed by class.  This can get you access to any @Provider:
    * MessageBodyReader/Writer or ExceptionMapper
    */
   public <T> T getProvider(Class<T> providerClass)
   {
      return (T) providers.get(providerClass);
   }

   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return exceptionMappers.get(type);
   }

   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      List<SortedKey<MessageBodyWriter>> writers = messageBodyWriters.getPossible(mediaType, type);
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

   protected <T> T getProviderInstance(Class<? extends T> clazz)
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
