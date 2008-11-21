package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.core.ResourceMethodCacheControlInterceptor;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.ResourceMethodInterceptor;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.EntityTagDelegate;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.jboss.resteasy.plugins.delegates.UriHeaderDelegate;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.specimpl.VariantListBuilderImpl;
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
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyProviderFactory extends RuntimeDelegate implements Providers
{
   /**
    * Allow us to sort message body implementations that are more specific for their types
    * i.e. MessageBodyWriter<Object> is less specific than MessageBodyWriter<String>.
    * <p/>
    * This helps out a lot when the desired media type is a wildcard and to weed out all the possible
    * default mappings.
    */
   private static class MessageBodyKey<T> implements Comparable<MessageBodyKey<T>>
   {
      public Class<? extends T> readerClass;
      public T obj;

      boolean isGeneric = false;

      private MessageBodyKey(Class<? extends T> readerClass, T reader)
      {
         this.readerClass = readerClass;
         this.obj = reader;
         // check the super class for the generic type 1st
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
      }

      public int compareTo(MessageBodyKey<T> tMessageBodyKey)
      {
         if (this == tMessageBodyKey) return 0;
         if (isGeneric == tMessageBodyKey.isGeneric) return 0;
         if (isGeneric) return 1;
         return -1;
      }
   }


   private MediaTypeMap<MessageBodyKey<MessageBodyReader>> messageBodyReaders = new MediaTypeMap<MessageBodyKey<MessageBodyReader>>();
   private MediaTypeMap<MessageBodyKey<MessageBodyWriter>> messageBodyWriters = new MediaTypeMap<MessageBodyKey<MessageBodyWriter>>();
   private Map<Class<?>, ExceptionMapper> exceptionMappers = new HashMap<Class<?>, ExceptionMapper>();
   private Map<Class<?>, Object> providers = new HashMap<Class<?>, Object>();
   private Map<Class<?>, MediaTypeMap<ContextResolver>> contextResolvers = new HashMap<Class<?>, MediaTypeMap<ContextResolver>>();
   private Map<Class<?>, StringConverter> stringConverters = new HashMap<Class<?>, StringConverter>();

   private Map<Class<?>, HeaderDelegate> headerDelegates = new HashMap<Class<?>, HeaderDelegate>();

   private static AtomicReference<ResteasyProviderFactory> pfr = new AtomicReference<ResteasyProviderFactory>();
   private static ThreadLocal<Map<Class<?>, Object>> contextualData = new ThreadLocal<Map<Class<?>, Object>>();
   private InterceptorRegistry interceptorRegistry = new InterceptorRegistry();

   public static void pushContext(Class<?> type, Object data)
   {
      Map<Class<?>, Object> map = getContextDataMap();
      map.put(type, data);
   }

   public static void pushContextDataMap(Map<Class<?>, Object> map)
   {
      contextualData.set(map);
   }

   public static Map<Class<?>, Object> getContextDataMap()
   {
      Map<Class<?>, Object> map = contextualData.get();
      if (map == null)
      {
         map = new HashMap<Class<?>, Object>();
         contextualData.set(map);
      }
      return map;
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) getContextDataMap().get(type);
   }

   public static <T> T popContextData(Class<T> type)
   {
      return (T) contextualData.get().remove(type);
   }

   public static void clearContextData()
   {
      contextualData.set(null);
   }

   public static void setInstance(ResteasyProviderFactory factory)
   {
      pfr.set(factory);
      RuntimeDelegate.setInstance(factory);
   }

   public static ResteasyProviderFactory getInstance()
   {
      return pfr.get();
   }

   public static ResteasyProviderFactory initializeInstance()
   {
      setInstance(new ResteasyProviderFactory());
      return getInstance();
   }

   public ResteasyProviderFactory()
   {
      addHeaderDelegate(MediaType.class, new MediaTypeHeaderDelegate());
      addHeaderDelegate(NewCookie.class, new NewCookieHeaderDelegate());
      addHeaderDelegate(Cookie.class, new CookieHeaderDelegate());
      addHeaderDelegate(URI.class, new UriHeaderDelegate());
      addHeaderDelegate(EntityTag.class, new EntityTagDelegate());
      addHeaderDelegate(CacheControl.class, new CacheControlDelegate());
      addHeaderDelegate(Locale.class, new LocaleDelegate());
      interceptorRegistry.registerResourceMethodInterceptor(ResourceMethodCacheControlInterceptor.class);
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
      MessageBodyReader reader = null;
      try
      {
         reader = provider.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      addMessageBodyReader(reader);
   }

   public void addMessageBodyReader(MessageBodyReader provider)
   {
      PropertyInjectorImpl injector = new PropertyInjectorImpl(provider.getClass(), this);
      injector.inject(provider);
      providers.put(provider.getClass(), provider);
      Consumes consumeMime = provider.getClass().getAnnotation(Consumes.class);
      MessageBodyKey<MessageBodyReader> key = new MessageBodyKey<MessageBodyReader>(provider.getClass(), provider);
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
      MessageBodyWriter writer = null;
      try
      {
         writer = provider.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      addMessageBodyWriter(writer);
   }

   public void addMessageBodyWriter(MessageBodyWriter provider)
   {
      PropertyInjectorImpl injector = new PropertyInjectorImpl(provider.getClass(), this);
      providers.put(provider.getClass(), provider);
      injector.inject(provider);
      Produces consumeMime = provider.getClass().getAnnotation(Produces.class);
      MessageBodyKey<MessageBodyWriter> key = new MessageBodyKey<MessageBodyWriter>(provider.getClass(), provider);
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
      List<MessageBodyKey<MessageBodyReader>> readers = messageBodyReaders.getPossible(mediaType);

      // if the desired media type is */* then sort the readers by their parameterized type to weed out less generic types
      // This helps with default mappings
      if (mediaType.isWildcardType()) Collections.sort(readers);
      for (MessageBodyKey<MessageBodyReader> reader : readers)
      {
         if (reader.obj.isReadable(type, genericType, annotations, mediaType))
         {
            return (MessageBodyReader<T>) reader.obj;
         }
      }
      return null;
   }

   public void addExceptionMapper(Class<? extends ExceptionMapper> provider)
   {
      ExceptionMapper writer = null;
      try
      {
         writer = provider.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      addExceptionMapper(writer);
   }

   public void addExceptionMapper(ExceptionMapper provider)
   {
      providers.put(provider.getClass(), provider);
      PropertyInjectorImpl injector = new PropertyInjectorImpl(provider.getClass(), this);
      injector.inject(provider);
      Type[] intfs = provider.getClass().getGenericInterfaces();
      for (Type type : intfs)
      {
         if (type instanceof ParameterizedType)
         {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType().equals(ExceptionMapper.class))
            {
               exceptionMappers.put(Types.getRawType(pt.getActualTypeArguments()[0]), provider);
            }
         }
      }

   }

   public void addContextResolver(Class<? extends ContextResolver> resolver)
   {
      ContextResolver writer = null;
      try
      {
         writer = resolver.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      addContextResolver(writer);
   }

   public void addContextResolver(ContextResolver provider)
   {
      providers.put(provider.getClass(), provider);
      PropertyInjectorImpl injector = new PropertyInjectorImpl(provider.getClass(), this);
      injector.inject(provider);
      Type[] intfs = provider.getClass().getGenericInterfaces();
      for (Type type : intfs)
      {
         if (type instanceof ParameterizedType)
         {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType().equals(ContextResolver.class))
            {
               Class<?> aClass = Types.getRawType(pt.getActualTypeArguments()[0]);
               MediaTypeMap<ContextResolver> resolvers = contextResolvers.get(aClass);
               if (resolvers == null)
               {
                  resolvers = new MediaTypeMap<ContextResolver>();
                  contextResolvers.put(aClass, resolvers);
               }
               Produces produces = provider.getClass().getAnnotation(Produces.class);
               if (produces != null)
               {
                  for (String produce : produces.value())
                  {
                     MediaType mime = MediaType.valueOf(produce);
                     resolvers.add(mime, provider);
                  }
               }
               else
               {
                  resolvers.add(new MediaType("*", "*"), provider);
               }
            }
         }
      }
   }

   public void addStringConverter(Class<? extends StringConverter> resolver)
   {
      StringConverter writer = null;
      try
      {
         writer = resolver.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      addStringConverter(writer);
   }

   public void addStringConverter(StringConverter provider)
   {
      providers.put(provider.getClass(), provider);
      PropertyInjectorImpl injector = new PropertyInjectorImpl(provider.getClass(), this);
      injector.inject(provider);
      Class clazz = provider.getClass();
      do {
         Type[] intfs = clazz.getGenericInterfaces();
         for (Type type : intfs)
         {
            if (type instanceof ParameterizedType)
            {
               ParameterizedType pt = (ParameterizedType) type;
               if (pt.getRawType().equals(StringConverter.class))
               {
                  Class<?> aClass = Types.getRawType(pt.getActualTypeArguments()[0]);
                  stringConverters.put(aClass, provider);
                  return;
               }
            }
         }
         clazz = clazz.getSuperclass();
      } while (clazz != null);
   }

   public List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type)
   {
      MediaTypeMap<ContextResolver> resolvers = contextResolvers.get(clazz);
      if (resolvers == null) return null;
      return resolvers.getPossible(type);
   }

   public StringConverter getStringConverter(Class<?> clazz)
   {
      if (stringConverters.size() == 0) return null;
      return stringConverters.get(clazz);
   }


   /**
    * Register a @Provider class.  Can be a MessageBodyReader/Writer or ExceptionMapper.
    *
    * @param provider
    */
   public void registerProvider(Class provider)
   {
      if (MessageBodyReader.class.isAssignableFrom(provider))
      {
         try
         {
            addMessageBodyReader(provider);
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
            addMessageBodyWriter(provider);
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
      if (ResourceMethodInterceptor.class.isAssignableFrom(provider))
      {
         interceptorRegistry.registerResourceMethodInterceptor(provider);
      }
      if (ContextResolver.class.isAssignableFrom(provider))
      {
         try
         {
            addContextResolver(provider);
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
      if (provider instanceof ResourceMethodInterceptor)
      {
         interceptorRegistry.registerResourceMethodInterceptor((ResourceMethodInterceptor) provider);
      }
      if (provider instanceof StringConverter)
      {
         addStringConverter((StringConverter) provider);
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
      List<MessageBodyKey<MessageBodyWriter>> writers = messageBodyWriters.getPossible(mediaType);
      // if the desired media type is */* then sort the readers by their parameterized type to weed out less generic types
      // This helps with default mappings
      if (mediaType.isWildcardType()) Collections.sort(writers);
      for (MessageBodyKey<MessageBodyWriter> writer : writers)
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

   public InterceptorRegistry getInterceptorRegistry()
   {
      return interceptorRegistry;
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
}
