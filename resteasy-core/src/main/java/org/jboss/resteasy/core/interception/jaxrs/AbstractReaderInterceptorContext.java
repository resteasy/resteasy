package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.InterceptorTimestampPair;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractReaderInterceptorContext implements ReaderInterceptorContext
{
   protected RESTEasyTracingLogger tracingLogger;
   private InterceptorTimestampPair<ReaderInterceptor> lastTracedInterceptor;
   protected ReaderInterceptor[] interceptors;
   protected ResteasyProviderFactory providerFactory;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, String> headers;
   protected InputStream inputStream;
   protected int index = 0;

   @Deprecated
   public AbstractReaderInterceptorContext(MediaType mediaType, ResteasyProviderFactory providerFactory, Annotation[] annotations, ReaderInterceptor[] interceptors, MultivaluedMap<String, String> headers, Type genericType, Class type, InputStream inputStream)
   {
      this.mediaType = mediaType;
      this.annotations = annotations;
      this.interceptors = interceptors;
      this.headers = headers;
      this.genericType = genericType;
      this.type = type;
      this.inputStream = inputStream;
      this.providerFactory = providerFactory;
      this.tracingLogger = RESTEasyTracingLogger.empty();
   }

   public AbstractReaderInterceptorContext(MediaType mediaType, ResteasyProviderFactory providerFactory, Annotation[] annotations, ReaderInterceptor[] interceptors, MultivaluedMap<String, String> headers, Type genericType, Class type, InputStream inputStream, RESTEasyTracingLogger logger)
   {
      this.mediaType = mediaType;
      this.annotations = annotations;
      this.interceptors = interceptors;
      this.headers = headers;
      this.genericType = genericType;
      this.type = type;
      this.inputStream = inputStream;
      this.providerFactory = providerFactory;
      this.tracingLogger = logger;
      if (logger == null) {
         this.tracingLogger = RESTEasyTracingLogger.empty();
      }
   }


   @Override
   public Object proceed() throws IOException
   {
      LogMessages.LOGGER.debugf("Interceptor Context: %s,  Method : proceed", getClass().getName());
      if (interceptors == null || index >= interceptors.length)
      {
         MessageBodyReader reader = getReader();
         if (reader!=null) {
            tracingLogger.log("MBR_READ_FROM", reader.getClass().getName());
            LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
         }
         return readFrom(reader);
      }
      LogMessages.LOGGER.debugf("ReaderInterceptor: %s", interceptors[index].getClass().getName());

      int x = index;
      traceBefore(interceptors[x]);

      try {
         return interceptors[index++].aroundReadFrom(this);
      } finally {
         traceAfter(interceptors[x]);
      }



      // index--;  we used to pop the index, but the TCK does not like this
   }

   @SuppressWarnings(value = "unchecked")
   protected Object readFrom(MessageBodyReader reader) throws IOException
   {
      return reader.readFrom(type, genericType, annotations, mediaType, headers, inputStream);
   }

   protected final void traceBefore(final ReaderInterceptor interceptor) {
      if (tracingLogger.isLogEnabled("RI_BEFORE")) {
         if ((lastTracedInterceptor != null) && (interceptor != null)) {
            tracingLogger.logDuration("RI_BEFORE", lastTracedInterceptor.getTimestamp(), lastTracedInterceptor.getInterceptor());
         }
         lastTracedInterceptor = new InterceptorTimestampPair<>(interceptor, System.nanoTime());
      }
   }

   protected final void traceAfter(final ReaderInterceptor interceptor) {
      if (tracingLogger.isLogEnabled("RI_AFTER")) {
         if ((lastTracedInterceptor != null) && (lastTracedInterceptor.getInterceptor() != null)) {
            tracingLogger.logDuration("RI_AFTER", lastTracedInterceptor.getTimestamp(), interceptor);
         }
         lastTracedInterceptor = new InterceptorTimestampPair<>(interceptor, System.nanoTime());
      }
   }

   protected MessageBodyReader getReader()
   {
      MediaType mediaType = this.mediaType;
      // spec says set to octet stream
      if (getHeaders() != null && getHeaders().getFirst(HttpHeaders.CONTENT_TYPE) == null && mediaType.isWildcardType())
      {
         mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
      }
      MessageBodyReader reader = resolveReader(mediaType);
      if (reader == null)
      {
         throwReaderNotFound();
      }
      return reader;
   }

   protected abstract MessageBodyReader resolveReader(MediaType mediaType);

   abstract protected void throwReaderNotFound();

   @Override
   public InputStream getInputStream()
   {
      return inputStream;
   }

   @Override
   public void setInputStream(InputStream is)
   {
      this.inputStream = is;
   }

   @Override
   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   @Override
   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   @Override
   public void setAnnotations(Annotation[] annotations)
   {
      if (annotations == null) throw new NullPointerException(Messages.MESSAGES.annotationsParamNull());
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

   public Object getProcessedInterceptorCount() {
         return index;
   }
}
