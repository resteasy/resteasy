package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

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
   protected Object entity;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, Object> headers;
   protected OutputStream outputStream;
   protected int index = 0;
   protected ResteasyProviderFactory providerFactory;

   public AbstractWriterInterceptorContext(WriterInterceptor[] interceptors, Annotation[] annotations, Object entity, Type genericType, MediaType mediaType, Class type, OutputStream outputStream, ResteasyProviderFactory providerFactory, MultivaluedMap<String, Object> headers)
   {
      this.providerFactory = providerFactory;
      this.interceptors = interceptors;
      this.annotations = annotations;
      this.entity = entity;
      this.genericType = genericType;
      this.mediaType = mediaType;
      this.type = type;
      this.outputStream = outputStream;
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
      if (annotations == null) throw new NullPointerException(Messages.MESSAGES.annotationsParamNull());
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
      LogMessages.LOGGER.debugf("Interceptor Context: %s,  Method : proceed", getClass().getName());

      if (interceptors == null || index >= interceptors.length)
      {
         MessageBodyWriter writer = getWriter();
         if (writer!=null)
             LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
         writeTo(writer);
      }
      else
      {
         LogMessages.LOGGER.debugf("WriterInterceptor: %s", interceptors[index].getClass().getName());
         interceptors[index++].aroundWriteTo(this);
         // we used to pop the index, but the TCK doesn't like this
      }
   }

   @SuppressWarnings(value = "unchecked")
   protected void writeTo(MessageBodyWriter writer) throws IOException
   {
      writer.writeTo(entity, type, genericType, annotations, mediaType, headers, outputStream);
   }

   protected MessageBodyWriter getWriter()
   {
      MessageBodyWriter writer = resolveWriter();

      if (writer == null)
      {
         throwWriterNotFoundException();
      }
      return writer;

   }

   abstract protected MessageBodyWriter resolveWriter();

   abstract void throwWriterNotFoundException();
}
