package org.jboss.resteasy.core.interception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractWriterInterceptorContext implements WriterInterceptorContext
{
   protected WriterInterceptor[] interceptors;
   protected MessageBodyWriter writer;
   protected Object entity;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, Object> headers;
   protected OutputStream outputStream;
   protected int index = 0;

   public AbstractWriterInterceptorContext(WriterInterceptor[] interceptors, Annotation[] annotations, Object entity, Type genericType, MediaType mediaType, Class type, OutputStream outputStream, MessageBodyWriter writer, MultivaluedMap<String, Object> headers)
   {
      this.interceptors = interceptors;
      this.annotations = annotations;
      this.entity = entity;
      this.genericType = genericType;
      this.mediaType = mediaType;
      this.type = type;
      this.outputStream = outputStream;
      this.writer = writer;
      this.headers = headers;
   }

   public Object getEntity()
   {
      return entity;
   }

   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   public Class getType()
   {
      return type;
   }

   public void setType(Class type)
   {
      this.type = type;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      if (annotations == null) throw new NullPointerException("annotations param was null");
      this.annotations = annotations;
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }

   public MultivaluedMap<String, Object> getHeaders()
   {
      return headers;
   }

   public OutputStream getOutputStream()
   {
      return outputStream;
   }

   public void setOutputStream(OutputStream outputStream)
   {
      this.outputStream = outputStream;
   }

   public void proceed() throws IOException, WebApplicationException
   {
      if (interceptors == null || index >= interceptors.length)
      {
         writer.writeTo(entity, type, genericType, annotations, mediaType, headers, outputStream);
      }
      else
      {
         interceptors[index++].aroundWriteTo(this);
         // we used to pop the index, but the TCK doesn't like this
      }
   }
}
