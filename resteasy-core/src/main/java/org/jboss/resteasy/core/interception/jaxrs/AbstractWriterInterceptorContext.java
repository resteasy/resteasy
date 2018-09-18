package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.InterceptorTimestampPair;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

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
   protected RESTEasyTracingLogger tracingLogger;
   protected WriterInterceptor[] interceptors;
   protected Object entity;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, Object> headers;
   protected OutputStream outputStream;

   public int getProcessedInterceptorCount() {
      return index;
   }

   protected int index = 0;
   protected ResteasyProviderFactory providerFactory;
   private InterceptorTimestampPair<WriterInterceptor> lastTracedInterceptor;

   // We need tracing logger to log the proceed event.
   // So the new constructor with logger should be used.
   @Deprecated
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
      this.tracingLogger = RESTEasyTracingLogger.empty();
   }

   public AbstractWriterInterceptorContext(WriterInterceptor[] interceptors, Annotation[] annotations, Object entity, Type genericType, MediaType mediaType, Class type, OutputStream outputStream, ResteasyProviderFactory providerFactory, MultivaluedMap<String, Object> headers, RESTEasyTracingLogger logger) {
      this.providerFactory = providerFactory;
      this.interceptors = interceptors;
      this.annotations = annotations;
      this.entity = entity;
      this.genericType = genericType;
      this.mediaType = mediaType;
      this.type = type;
      this.outputStream = outputStream;
      this.headers = headers;
      this.tracingLogger = logger;
      if (logger == null) {
         this.tracingLogger = RESTEasyTracingLogger.empty();
      }
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
         if (writer!=null) {
            tracingLogger.log("MBW_WRITE_TO", writer.getClass().getName());
            LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
         }
         writeTo(writer);
      }
      else
      {
         LogMessages.LOGGER.debugf("WriterInterceptor: %s", interceptors[index].getClass().getName());
         int x = index;
         traceBefore(interceptors[x]);

         try {
            interceptors[index++].aroundWriteTo(this);
         } finally {
            traceAfter(interceptors[x]);
         }
         // we used to pop the index, but the TCK doesn't like this
      }
   }

   protected final void traceBefore(final WriterInterceptor interceptor) {
      if (tracingLogger.isLogEnabled("WI_BEFORE")) {
         if ((lastTracedInterceptor != null) && (interceptor != null)) {
            tracingLogger.logDuration("WI_BEFORE", lastTracedInterceptor.getTimestamp(), lastTracedInterceptor.getInterceptor());
         }
         lastTracedInterceptor = new InterceptorTimestampPair<>(interceptor, System.nanoTime());
      }
   }

   protected final void traceAfter(final WriterInterceptor interceptor) {
      if (tracingLogger.isLogEnabled("WI_AFTER")) {
         if ((lastTracedInterceptor != null) && (lastTracedInterceptor.getInterceptor() != null)) {
            tracingLogger.logDuration("WI_AFTER", lastTracedInterceptor.getTimestamp(), interceptor);
         }
         lastTracedInterceptor = new InterceptorTimestampPair<>(interceptor, System.nanoTime());
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
