package org.jboss.resteasy.core.interception.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public class ServerWriterInterceptorContext extends AbstractWriterInterceptorContext
{
   private HttpRequest request;
   private Consumer<Throwable> onWriteComplete;

   public ServerWriterInterceptorContext(final WriterInterceptor[] interceptors, final ResteasyProviderFactory providerFactory,
                                         final Object entity, final Class type, final Type genericType, final Annotation[] annotations,
                                         final MediaType mediaType, final MultivaluedMap<String, Object> headers,
                                         final OutputStream outputStream,
                                         final HttpRequest request, final Consumer<Throwable> onWriteComplete)
   {
      // server side must use request instead of provider factory to get tracing logger.
      super(interceptors, annotations, entity, genericType, mediaType, type, outputStream, providerFactory, headers, RESTEasyTracingLogger.getInstance(request));
      this.request = request;
      this.onWriteComplete = onWriteComplete;
   }

   @SuppressWarnings(value = "unchecked")
   @Override
   protected MessageBodyWriter resolveWriter()
   {
      return ((ResteasyProviderFactoryImpl)providerFactory).getServerMessageBodyWriter(
            type, genericType, annotations, mediaType, tracingLogger);
   }

   @Override
   void throwWriterNotFoundException()
   {
      throw new NoMessageBodyWriterFoundFailure(type, mediaType);
   }

   @Override
   public Object getProperty(String name)
   {
      return request.getAttribute(name);
   }

   @Override
   public CompletionStage<Void> getStarted()
   {
      return aroundWriteTo(() -> super.getStarted());
   }

   @SuppressWarnings(value = "unchecked")
   protected CompletionStage<Void> writeTo(MessageBodyWriter writer) throws IOException
   {
      return request.getAsyncContext().executeBlockingIo(() -> writer.writeTo(entity, type, genericType, annotations, mediaType, headers, outputStream),
            interceptors != null && interceptors.length > 0);
   }

   @SuppressWarnings(value = "unchecked")
   protected CompletionStage<Void> writeTo(AsyncMessageBodyWriter writer)
   {
      return request.getAsyncContext().executeAsyncIo(
            writer.asyncWriteTo(entity, type, genericType, annotations, mediaType, headers, (AsyncOutputStream)outputStream));
   }

   private CompletionStage<Void> aroundWriteTo(Supplier<CompletionStage<Void>> ret)
   {
      boolean startedSuspended = request.getAsyncContext().isSuspended();
      return ret.get().whenComplete((v, t) -> {
         // make sure we unwrap these horrors
         if(t instanceof CompletionException)
            t = t.getCause();
         onWriteComplete.accept(t);
         // make sure we complete any async request after we've written the body or exception
         if(!startedSuspended && request.getAsyncContext().isSuspended()) {
            request.getAsyncContext().complete();
         }
      });
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      ArrayList<String> names = new ArrayList<String>();
      Enumeration<String> enames = request.getAttributeNames();
      while (enames.hasMoreElements())
      {
         names.add(enames.nextElement());
      }
      return names;
   }

   @Override
   public void setProperty(String name, Object object)
   {
      if (object == null)
      {
         request.removeAttribute(name);
      }
      else
      {
         request.setAttribute(name, object);
      }
   }

   @Override
   public void removeProperty(String name)
   {
      request.removeAttribute(name);
   }
}
