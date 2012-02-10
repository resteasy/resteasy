package org.jboss.resteasy.core.filter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WriterInterceptorContextImpl implements WriterInterceptorContext
{
   protected int index = 0;
   protected WriterInterceptor[] interceptors;
   protected MessageBodyWriter writer;
   protected Object entity;
   protected Class<?> type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, Object> headers;
   protected OutputStream outputStream;
   protected Map<String, Object> properties;

   public WriterInterceptorContextImpl(Object entity, Class<?> type,
                                       Type genericType,
                                       Annotation[] annotations,
                                       MediaType mediaType,
                                       MultivaluedMap<String, Object> headers,
                                       OutputStream outputStream,
                                       WriterInterceptor[] interceptors,
                                       MessageBodyWriter writer,
                                       Map<String, Object> properties)
   {
      this.entity = entity;
      this.type = type;
      this.genericType = genericType;
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.headers = headers;
      this.outputStream = outputStream;
      this.interceptors = interceptors;
      this.writer = writer;
      this.properties = properties;
   }

   @Override
   public void proceed() throws IOException
   {
      if (interceptors == null || index >= interceptors.length)
      {
         writer.writeTo(entity, type, genericType, annotations, mediaType, headers, outputStream);
      }
      else
      {
         try
         {
            interceptors[index++].aroundWriteTo(this);
         }
         finally
         {
            index--;
         }
      }
   }

   @Override
   public Object getEntity()
   {
      return entity;
   }

   @Override
   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   @Override
   public OutputStream getOutputStream()
   {
      return outputStream;
   }

   @Override
   public void setOutputStream(OutputStream os)
   {
      this.outputStream = os;
   }

   @Override
   public MultivaluedMap<String, Object> getHeaders()
   {
      return headers;
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return null;
   }

   @Override
   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   @Override
   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   @Override
   public Class getType()
   {
      return type;
   }

   @Override
   public void setType(Class type)
   {
      this.type = type;
   }

   @Override
   public Type getGenericType()
   {
      return genericType;
   }

   @Override
   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   @Override
   public MediaType getMediaType()
   {
      return mediaType;
   }

   @Override
   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }
}
