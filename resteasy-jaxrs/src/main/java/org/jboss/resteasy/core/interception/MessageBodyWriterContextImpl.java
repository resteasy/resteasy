package org.jboss.resteasy.core.interception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyWriterContextImpl implements MessageBodyWriterContext
{
   protected MessageBodyWriterInterceptor[] interceptors;
   protected MessageBodyWriter writer;
   protected Object entity;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, Object> headers;
   protected OutputStream outputStream;
   protected int index = 0;

   public MessageBodyWriterContextImpl(MessageBodyWriterInterceptor[] interceptors, MessageBodyWriter writer,
                                       Object entity, Class type, Type genericType, Annotation[] annotations,
                                       MediaType mediaType, MultivaluedMap<String, Object> headers,
                                       OutputStream outputStream)
   {
      this.interceptors = interceptors;
      this.writer = writer;
      this.entity = entity;
      this.type = type;
      this.genericType = genericType;
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.headers = headers;
      this.outputStream = outputStream;
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
         try
         {
            interceptors[index++].write(this);
         }
         finally
         {
            index--;
         }
      }
   }
}