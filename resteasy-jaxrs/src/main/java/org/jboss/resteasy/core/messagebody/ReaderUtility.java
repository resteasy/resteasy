package org.jboss.resteasy.core.messagebody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.core.interception.MessageBodyReaderContextImpl;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Utility for accessing RESTEasy's MessageBodyReader setup
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

@SuppressWarnings("unchecked")
public abstract class ReaderUtility
{
   private ResteasyProviderFactory factory;
   private MessageBodyReaderInterceptor[] interceptors;

   public static <T> T read(Class<T> type, String contentType, String buffer)
         throws IOException
   {
      return read(type, contentType,buffer.getBytes());
   }

   public static <T> T read(Class<T> type, String contentType, byte[] buffer)
         throws IOException
   {
      return read(type, MediaType.valueOf(contentType),
            new ByteArrayInputStream(buffer));
   }

   public static <T> T read(Class<T> type, MediaType mediaType, byte[] buffer)
         throws IOException
   {
      return read(type, mediaType, new ByteArrayInputStream(buffer));
   }

   public static <T> T read(Class<T> type, MediaType mt, InputStream is)
         throws IOException
   {
      return new ReaderUtility()
      {
         @Override
         public RuntimeException createReaderNotFound(Type genericType,
               MediaType mediaType)
         {
            throw new RuntimeException("Could not read type " + genericType
                  + " for media type " + mediaType);
         }
      }.doRead(type, mt, is);
   }

   public ReaderUtility()
   {
      this(ResteasyProviderFactory.getInstance(), null);
   }

   public ReaderUtility(ResteasyProviderFactory factory,
         MessageBodyReaderInterceptor[] interceptors)
   {
      this.factory = factory;
      this.interceptors = interceptors;
   }

   public <T> T doRead(Class<T> type, MediaType mediaType, InputStream is)
         throws IOException
   {
      return doRead(type, type, mediaType, null, null, is);
   }

   public <T> T doRead(Class<T> type, Type genericType, MediaType mediaType,
         MultivaluedMap<String, String> requestHeaders, InputStream is)
         throws IOException
   {
      return doRead(type, genericType, mediaType, null, requestHeaders, is);
   }

   public Object doRead(HttpRequest request, Class type, Type genericType,
         Annotation[] annotations, MediaType mediaType) throws IOException
   {
      return doRead(type, genericType, mediaType, annotations, request
            .getHttpHeaders().getRequestHeaders(), request.getInputStream());
   }

   public <T> T doRead(Class<T> type, Type genericType, MediaType mediaType,
         Annotation[] annotations,
         MultivaluedMap<String, String> requestHeaders, InputStream inputStream)
         throws IOException
   {
      MessageBodyReader reader = factory.getMessageBodyReader(type,
            genericType, annotations, mediaType);
      if (reader == null)
         throw createReaderNotFound(genericType, mediaType);
      return (T) new MessageBodyReaderContextImpl(interceptors, reader, type,
            genericType, annotations, mediaType, requestHeaders, inputStream)
            .proceed();
   }

   public abstract RuntimeException createReaderNotFound(Type genericType,
         MediaType mediaType);
}
