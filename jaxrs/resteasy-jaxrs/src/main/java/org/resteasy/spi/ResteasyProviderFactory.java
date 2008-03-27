package org.resteasy.spi;

import org.resteasy.MediaTypeMap;
import org.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.resteasy.plugins.delegates.EntityTagDelegate;
import org.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.resteasy.plugins.delegates.UriHeaderDelegate;
import org.resteasy.specimpl.ResponseBuilderImpl;
import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.specimpl.VariantListBuilderImpl;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.ApplicationConfig;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyProviderFactory extends RuntimeDelegate
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
         Type impls = readerClass.getGenericInterfaces()[0];
         if (impls instanceof ParameterizedType)
         {
            ParameterizedType param = (ParameterizedType) impls;
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
   private Map<Class<?>, HeaderDelegate> headerDelegates = new HashMap<Class<?>, HeaderDelegate>();

   private static AtomicReference<ResteasyProviderFactory> pfr = new AtomicReference<ResteasyProviderFactory>();
   private static ThreadLocal<Map<Class<?>, Object>> contextualData = new ThreadLocal<Map<Class<?>, Object>>();

   public static void pushContext(Class<?> type, Object data)
   {
      Map<Class<?>, Object> map = contextualData.get();
      if (map == null)
      {
         map = new HashMap<Class<?>, Object>();
         contextualData.set(map);
      }
      map.put(type, data);
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) contextualData.get().get(type);
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
      ConsumeMime consumeMime = provider.getAnnotation(ConsumeMime.class);
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
      MessageBodyKey<MessageBodyReader> key = new MessageBodyKey<MessageBodyReader>(provider, reader);
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.parse(consume);
            messageBodyReaders.add(mime, key);
         }
      }
      else
      {
         messageBodyReaders.add(new MediaType("*", "*"), key);
      }
   }

   public void addMessageBodyReader(MessageBodyReader provider)
   {
      ConsumeMime consumeMime = provider.getClass().getAnnotation(ConsumeMime.class);
      MessageBodyKey<MessageBodyReader> key = new MessageBodyKey<MessageBodyReader>(provider.getClass(), provider);
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.parse(consume);
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
      ProduceMime consumeMime = provider.getAnnotation(ProduceMime.class);
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
      MessageBodyKey<MessageBodyWriter> key = new MessageBodyKey<MessageBodyWriter>(provider, writer);
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.parse(consume);
            messageBodyWriters.add(mime, key);
         }
      }
      else
      {
         messageBodyWriters.add(new MediaType("*", "*"), key);
      }
   }

   public void addMessageBodyWriter(MessageBodyWriter provider)
   {
      ProduceMime consumeMime = provider.getClass().getAnnotation(ProduceMime.class);
      MessageBodyKey<MessageBodyWriter> key = new MessageBodyKey<MessageBodyWriter>(provider.getClass(), provider);
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.parse(consume);
            messageBodyWriters.add(mime, key);
         }
      }
      else
      {
         messageBodyWriters.add(new MediaType("*", "*"), key);
      }
   }

   public <T> MessageBodyReader<T> createMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      List<MessageBodyKey<MessageBodyReader>> readers = messageBodyReaders.getPossible(mediaType);

      // if the desired media type is */* then sort the readers by their parameterized type to weed out less generic types
      // This helps with default mappings
      if (mediaType.isWildcardType()) Collections.sort(readers);
      for (MessageBodyKey<MessageBodyReader> reader : readers)
      {
         if (reader.obj.isReadable(type, genericType, annotations))
         {
            return (MessageBodyReader<T>) reader.obj;
         }
      }
      return null;
   }

   public <T> MessageBodyWriter<T> createMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      List<MessageBodyKey<MessageBodyWriter>> writers = messageBodyWriters.getPossible(mediaType);
      // if the desired media type is */* then sort the readers by their parameterized type to weed out less generic types
      // This helps with default mappings
      if (mediaType.isWildcardType()) Collections.sort(writers);
      for (MessageBodyKey<MessageBodyWriter> writer : writers)
      {
         //System.out.println("matching: " + writer.obj.getClass());
         if (writer.obj.isWriteable(type, genericType, annotations))
         {
            return (MessageBodyWriter<T>) writer.obj;
         }
      }
      return null;
   }

   public <T> T createEndpoint(ApplicationConfig applicationConfig, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException
   {
      return null;
   }
}
