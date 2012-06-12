package org.jboss.resteasy.core.messagebody;

import org.jboss.resteasy.core.interception.WriterInterceptorContextImpl;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for accessing RESTEasy's MessageBodyWrite setup
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

@SuppressWarnings("unchecked")
public abstract class WriterUtility
{
   private ResteasyProviderFactory factory;
   private WriterInterceptor[] interceptors;

   public static String asString(Object toOutput, String contentType)
           throws IOException
   {
      return new String(getBytes(toOutput, contentType));
   }

   public static byte[] getBytes(Object toOutput, String contentType)
           throws IOException
   {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      write(toOutput, MediaType.valueOf(contentType), bos);
      return bos.toByteArray();
   }

   public static void write(Object toOutput, MediaType mt, OutputStream os)
           throws IOException
   {
      new WriterUtility()
      {
         @Override
         public RuntimeException createWriterNotFound(Type genericType,
                                                      MediaType mediaType)
         {
            throw new RuntimeException("Could not read type " + genericType
                    + " for media type " + mediaType);
         }
      }.doWrite(toOutput, mt, os);
   }

   public WriterUtility()
   {
      this(ResteasyProviderFactory.getInstance(), null);
   }

   public WriterUtility(ResteasyProviderFactory factory,
                        WriterInterceptor[] interceptors)
   {
      this.factory = factory;
      this.interceptors = interceptors;
   }

   public void doWrite(Object toOutput, MediaType mediaType, OutputStream os)
           throws IOException
   {
      doWrite(toOutput, toOutput.getClass(), mediaType, os);
   }


   public void doWrite(Object toOutput, Class type, MediaType mediaType, OutputStream os)
           throws IOException
   {
      doWrite(toOutput, type, type, mediaType, null, null, os);
   }

   public void doWrite(Object toOutput, Class type, Type genericType, MediaType mediaType,
                       MultivaluedMap<String, Object> requestHeaders, OutputStream os)
           throws IOException
   {
      doWrite(toOutput, type, genericType, mediaType, null, requestHeaders, os);
   }

   public void doWrite(HttpResponse response, Object toOutput, Class type, Type genericType,
                       Annotation[] annotations, MediaType mediaType) throws IOException
   {
      doWrite(toOutput, type, genericType, mediaType, annotations, response
              .getOutputHeaders(), response.getOutputStream());
   }

   public void doWrite(Object toOutput, Class type, Type genericType,
                       MediaType mediaType, Annotation[] annotations,
                       MultivaluedMap<String, Object> requestHeaders,
                       OutputStream outputStream) throws IOException
   {
      MessageBodyWriter writer = factory.getMessageBodyWriter(type,
              genericType, annotations, mediaType);
      if (writer == null)
         throw createWriterNotFound(genericType, mediaType);

      final Map<String, Object> attributes = new HashMap<String, Object>();
      WriterInterceptorContextImpl messageBodyWriterContext = new WriterInterceptorContextImpl(interceptors, writer, toOutput, type,
              genericType, annotations, mediaType, requestHeaders, outputStream, attributes);
      messageBodyWriterContext
              .proceed();
   }

   public abstract RuntimeException createWriterNotFound(Type genericType,
                                                         MediaType mediaType);
}
