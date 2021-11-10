package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.InterceptorTimestampPair;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractWriterInterceptorContext implements WriterInterceptorContext, AsyncWriterInterceptorContext
{
   protected RESTEasyTracingLogger tracingLogger;
   protected WriterInterceptor[] interceptors;
   protected boolean requireAsyncIO;
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
   private InterceptorTimestampPair<?> lastTracedInterceptor;

   // We need tracing logger to log the proceed event.
   // So the new constructor with logger should be used.
   @Deprecated
   public AbstractWriterInterceptorContext(final WriterInterceptor[] interceptors, final Annotation[] annotations, final Object entity, final Type genericType, final MediaType mediaType, final Class type, final OutputStream outputStream, final ResteasyProviderFactory providerFactory, final MultivaluedMap<String, Object> headers)
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

   public AbstractWriterInterceptorContext(final WriterInterceptor[] interceptors, final Annotation[] annotations, final Object entity, final Type genericType, final MediaType mediaType, final Class type, final OutputStream outputStream, final ResteasyProviderFactory providerFactory, final MultivaluedMap<String, Object> headers, final RESTEasyTracingLogger logger) {
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

   public void setAsyncOutputStream(AsyncOutputStream asyncOutputStream) {
      this.outputStream = asyncOutputStream;
   }

   public AsyncOutputStream getAsyncOutputStream()
   {
      return (AsyncOutputStream) outputStream;
   }

   public CompletionStage<Void> getStarted() {
      if(outputStream instanceof AsyncOutputStream
            && getWriter() instanceof AsyncMessageBodyWriter
            && interceptorsSupportAsyncIo()) {
         requireAsyncIO = true;
         return asyncProceed();
      }
      try
      {
         return syncProceed();
      } catch (WebApplicationException | IOException e)
      {
         CompletableFuture<Void> ret = new CompletableFuture<>();
         ret.completeExceptionally(e);
         return ret;
      }
   }

   private boolean interceptorsSupportAsyncIo()
   {
      for (WriterInterceptor interceptor : interceptors)
      {
         if(interceptor instanceof AsyncWriterInterceptor == false)
            return false;
      }
      return true;
   }

   protected boolean preferAsyncIo() {
      return false;
   }

   public CompletionStage<Void> asyncProceed() {
      LogMessages.LOGGER.debugf("Interceptor Context: %s,  Method : proceed", getClass().getName());

      if (interceptors == null || index >= interceptors.length)
      {
         AsyncMessageBodyWriter writer = (AsyncMessageBodyWriter)getWriter();
         if (writer!=null) {
            tracingLogger.log("MBW_WRITE_TO", writer.getClass().getName());
            LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
         }
         return writeTo(writer);
      }
      else
      {
         LogMessages.LOGGER.debugf("WriterInterceptor: %s", interceptors[index].getClass().getName());
         int x = index;
         traceBefore(interceptors[x]);

         return ((AsyncWriterInterceptor) interceptors[index++]).asyncAroundWriteTo(this)
               .whenComplete((v, t) -> traceAfter(interceptors[x]));
         // we used to pop the index, but the TCK doesn't like this
      }

   }

   public void proceed() throws IOException, WebApplicationException
   {
      // we only get here if we have an interceptor, in which case we're never async, so let's block and
      // get any exception out
      try
      {
         syncProceed().toCompletableFuture().get();
      } catch (InterruptedException e)
      {
         SynchronousDispatcher.rethrow(e);
      } catch (ExecutionException e)
      {
         SynchronousDispatcher.rethrow(e.getCause());
      }
   }

   public CompletionStage<Void> syncProceed() throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Interceptor Context: %s,  Method : proceed", getClass().getName());

      if (interceptors == null || index >= interceptors.length)
      {
         MessageBodyWriter writer = getWriter();
         if (writer!=null) {
            tracingLogger.log("MBW_WRITE_TO", writer.getClass().getName());
            LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
         }
         return writeTo(writer);
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
         return CompletableFuture.completedFuture(null);
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
   protected CompletionStage<Void> writeTo(MessageBodyWriter writer) throws IOException
   {
       writer.writeTo(entity, type, genericType, annotations, mediaType, headers, outputStream);
       return CompletableFuture.completedFuture(null);
   }

   @SuppressWarnings(value = "unchecked")
   protected CompletionStage<Void> writeTo(AsyncMessageBodyWriter writer)
   {
       return writer.asyncWriteTo(entity, type, genericType, annotations, mediaType, headers, (AsyncOutputStream) outputStream);
   }

   protected MessageBodyWriter getWriter()
   {
      MessageBodyWriter writer = resolveWriter();
      if(requireAsyncIO && writer instanceof AsyncMessageBodyWriter == false)
         throw new IllegalStateException("Cannot switch body writer from blocking to asynchronous during writer interceptor run");

      if (writer == null)
      {
         throwWriterNotFoundException();
      }
      return writer;

   }

   protected abstract MessageBodyWriter resolveWriter();

   abstract void throwWriterNotFoundException();
}
