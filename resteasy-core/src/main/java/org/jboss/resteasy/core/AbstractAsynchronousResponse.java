package org.jboss.resteasy.core;

import org.jboss.resteasy.core.ResteasyContext.CloseableContext;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;

import jakarta.ws.rs.container.CompletionCallback;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.TimeoutHandler;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractAsynchronousResponse implements ResteasyAsynchronousResponse, ResourceMethodInvokerAwareResponse
{
   protected SynchronousDispatcher dispatcher;
   protected ResourceMethodInvoker method;
   protected HttpRequest request;
   protected HttpResponse response;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected AsyncWriterInterceptor[] asyncWriterInterceptors;
   protected Annotation[] annotations;
   protected TimeoutHandler timeoutHandler;
   protected List<CompletionCallback> completionCallbacks = new ArrayList<CompletionCallback>();
   protected Map<Class<?>, Object> contextDataMap;
   private boolean callbacksCalled;

   protected AbstractAsynchronousResponse(final SynchronousDispatcher dispatcher,final HttpRequest request,final HttpResponse response)
   {
      this.dispatcher = dispatcher;
      this.request = request;
      this.response = response;
      contextDataMap = ResteasyContext.getContextDataMap();
   }



   @Override
   public Collection<Class<?>> register(Class<?> callback) throws NullPointerException
   {
      if (callback == null) throw new NullPointerException(Messages.MESSAGES.callbackWasNull());
      Object cb = dispatcher.getProviderFactory().createProviderInstance(callback);
      return register(cb);
   }

   @Override
   public Collection<Class<?>> register(Object callback) throws NullPointerException
   {
      if (callback == null) throw new NullPointerException(Messages.MESSAGES.callbackWasNull());
      ArrayList<Class<?>> registered = new ArrayList<Class<?>>();
      if (callback instanceof CompletionCallback)
      {
         completionCallbacks.add((CompletionCallback) callback);
         registered.add(CompletionCallback.class);
      }
      return registered;
   }

   @Override
   public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback, Class<?>... callbacks) throws NullPointerException
   {
      Map<Class<?>, Collection<Class<?>>> map = new HashMap<Class<?>, Collection<Class<?>>>();
      map.put(callback, register(callback));
      for (Class<?> call : callbacks)
      {
         map.put(call, register(call));
      }
      return map;
   }

   @Override
   public Map<Class<?>, Collection<Class<?>>> register(Object callback, Object... callbacks) throws NullPointerException
   {
      Map<Class<?>, Collection<Class<?>>> map = new HashMap<Class<?>, Collection<Class<?>>>();
      map.put(callback.getClass(), register(callback));
      for (Object call : callbacks)
      {
         map.put(call.getClass(), register(call));
      }
      return map;
   }

   @Override
   public void setTimeoutHandler(TimeoutHandler handler)
   {
      this.timeoutHandler = handler;
   }

   @Override
   public ResourceMethodInvoker getMethod()
   {
      return method;
   }

   @Override
   public void setMethod(ResourceMethodInvoker method)
   {
      this.method = method;
   }

   @Override
   public ContainerResponseFilter[] getResponseFilters()
   {
      return responseFilters;
   }

   @Override
   public void setResponseFilters(ContainerResponseFilter[] responseFilters)
   {
      this.responseFilters = responseFilters;
   }

   @Override
   public WriterInterceptor[] getWriterInterceptors()
   {
      return writerInterceptors;
   }

   @Override
   public void setWriterInterceptors(WriterInterceptor[] writerInterceptors)
   {
      this.writerInterceptors = writerInterceptors;
   }

   @Override
   public AsyncWriterInterceptor[] getAsyncWriterInterceptors()
   {
      return asyncWriterInterceptors;
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
   public void completionCallbacks(Throwable throwable)
   {
      // make sure we only call them once
      if(callbacksCalled)
         return;
      callbacksCalled = true;
      for (CompletionCallback callback : completionCallbacks)
      {
         callback.onComplete(throwable);
      }
   }

   @Deprecated
   protected boolean internalResume(Object entity)
   {
      return internalResume(entity, t -> {});
   }

   protected boolean internalResume(Object entity, Consumer<Throwable> onComplete)
   {
      try(CloseableContext c = ResteasyContext.addCloseableContextDataLevel(contextDataMap)){
         Response response = null;
         if (entity == null)
         {
            response = Response.noContent().build();
         }
         else if (entity instanceof Response)
         {
            response = (Response) entity;
         }
         else
         {
            if (method == null) throw new IllegalStateException(Messages.MESSAGES.unknownMediaTypeResponseEntity());
            MediaType type = method.resolveContentType(request, entity);
            BuiltResponse jaxrsResponse = (BuiltResponse)Response.ok(entity, type).build();
            if (!(entity instanceof GenericEntity))
            {
               jaxrsResponse.setGenericType(method.getGenericReturnType());
            }
            jaxrsResponse.addMethodAnnotations(method.getMethodAnnotations());
            response = jaxrsResponse;
         }
         try
         {
            dispatcher.asynchronousDelivery(this.request, this.response, response, t -> {
               if(t != null)
               {
                  internalResume(t, t2 -> {
                     onComplete.accept(t);
                     // callbacks done by internalResume
                  });
               }
               else
               {
                  onComplete.accept(null);
                  completionCallbacks(null);
               }
            });
         }
         catch (Throwable e)
         {
            return internalResume(e, t -> {
               onComplete.accept(e);
               // callbacks done by internalResume
            });
         }
      }
      return true;
   }

   @Deprecated
   protected boolean internalResume(Throwable exc)
   {
      return internalResume(exc, t -> {});
   }

   protected boolean internalResume(Throwable exc, Consumer<Throwable> onComplete)
   {
      try(CloseableContext c = ResteasyContext.addCloseableContextDataLevel(contextDataMap)){
         dispatcher.asynchronousExceptionDelivery(request, response, exc, t -> {
            onComplete.accept(t);
            completionCallbacks(exc);
         });
      }
      return true;
   }

}
