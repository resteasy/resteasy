package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.WriterInterceptor;

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
public abstract class AbstractAsynchronousResponse implements ResteasyAsynchronousResponse
{
   protected SynchronousDispatcher dispatcher;
   protected ResourceMethodInvoker method;
   protected HttpRequest request;
   protected HttpResponse response;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected Annotation[] annotations;
   protected TimeoutHandler timeoutHandler;
   protected List<CompletionCallback> completionCallbacks = new ArrayList<CompletionCallback>();
   protected Map<Class<?>, Object> contextDataMap;

   protected AbstractAsynchronousResponse(SynchronousDispatcher dispatcher, HttpRequest request, HttpResponse response)
   {
      this.dispatcher = dispatcher;
      this.request = request;
      this.response = response;
      contextDataMap = ResteasyProviderFactory.getContextDataMap();
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
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
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
         jaxrsResponse.setGenericType(method.getGenericReturnType());
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
      return true;
   }

   @Deprecated
   protected boolean internalResume(Throwable exc)
   {
      return internalResume(exc, t -> {});
   }

   protected boolean internalResume(Throwable exc, Consumer<Throwable> onComplete)
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      dispatcher.asynchronousExceptionDelivery(request, response, exc, t -> {
         onComplete.accept(t);
         completionCallbacks(exc);
      });
      return true;
   }

}
