package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

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

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

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

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getEntity .")
   public Object getEntity()
   {
      return entity;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : setEntity .")
   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getType .")
   public Class getType()
   {
      return type;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : setType .")
   public void setType(Class type)
   {
      this.type = type;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getGenericType .")
   public Type getGenericType()
   {
      return genericType;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : setGenericType .")
   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getAnnotations .")
   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : setAnnotations .")
   public void setAnnotations(Annotation[] annotations)
   {
      if (annotations == null) throw new NullPointerException(Messages.MESSAGES.annotationsParamNull());
      this.annotations = annotations;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getMediaType .")
   public MediaType getMediaType()
   {
      return mediaType;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : setMediaType .")
   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getHeaders .")
   public MultivaluedMap<String, Object> getHeaders()
   {
      return headers;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getOutputStream .")
   public OutputStream getOutputStream()
   {
      return outputStream;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : setOutputStream .")
   public void setOutputStream(OutputStream outputStream)
   {
      this.outputStream = outputStream;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : proceed .")
   public void proceed() throws IOException, WebApplicationException
   {
      if (interceptors == null || index >= interceptors.length)
      {
         MessageBodyWriter writer = getWriter();
         writeTo(writer);
      }
      else
      {
         interceptors[index++].aroundWriteTo(this);
         // we used to pop the index, but the TCK doesn't like this
      }
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : writeTo .")
   protected void writeTo(MessageBodyWriter writer) throws IOException
   {
      writer.writeTo(entity, type, genericType, annotations, mediaType, headers, outputStream);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext , method call : getWriter .")
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
