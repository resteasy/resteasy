package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;

import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResumeCallback;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractAsynchronousResponse implements ResteasyAsynchronousResponse
{
   protected SynchronousDispatcher dispatcher;
   protected ResourceMethod method;
   protected HttpRequest request;
   protected HttpResponse response;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected Annotation[] annotations;
   protected TimeoutHandler timeoutHandler;
   protected List<ResumeCallback> resumeCallbacks = new ArrayList<ResumeCallback>();

   protected AbstractAsynchronousResponse(SynchronousDispatcher dispatcher, HttpRequest request, HttpResponse response)
   {
      this.dispatcher = dispatcher;
      this.request = request;
      this.response = response;
   }

   @Override
   public boolean register(Class<?> callback) throws NullPointerException
   {
      if (callback == null) throw new NullPointerException("Callback was null");
      Object cb = dispatcher.getProviderFactory().createProviderInstance(callback);
      return register(cb);
   }

   @Override
   public boolean register(Object callback) throws NullPointerException
   {
      if (callback == null) throw new NullPointerException("Callback was null");
      boolean registered = false;
      if (callback instanceof ResumeCallback)
      {
         registered = true;
         resumeCallbacks.add((ResumeCallback)callback);
      }
      return registered;
   }

   @Override
   public boolean[] register(Class<?> callback, Class<?>... callbacks) throws NullPointerException
   {
      boolean[] results = new boolean[1 + callbacks.length];
      results[0] = register(callback);
      for (int i = 0; i < callbacks.length; i++)
      {
         results[i + 1] = register(callbacks[i]);
      }
      return results;
   }

   @Override
   public boolean[] register(Object callback, Object... callbacks) throws NullPointerException
   {
      boolean[] results = new boolean[1 + callbacks.length];
      results[0] = register(callback);
      for (int i = 0; i < callbacks.length; i++)
      {
         results[i + 1] = register(callbacks[i]);
      }
      return results;
   }

   @Override
   public void setTimeoutHandler(TimeoutHandler handler)
   {
      this.timeoutHandler = handler;
   }

   @Override
   public ResourceMethod getMethod()
   {
      return method;
   }

   @Override
   public void setMethod(ResourceMethod method)
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

   protected void sendResponse(Response response) throws IllegalStateException
   {
      dispatcher.asynchronousDelivery(this.request, this.response, response);
   }

   protected void sendResponseObject(Object entity, int status)
   {
      if (entity == null)
      {
         sendResponse(Response.status(status).build());
      }
      else if (entity instanceof Response)
      {
         sendResponse((Response) entity);
      }
      else
      {
         if (method == null) throw new IllegalStateException("Unknown media type for response entity");
         MediaType type = method.resolveContentType(request, entity);
         sendResponse(Response.status(status).entity(entity).type(type).build());
      }

   }

   @Override
   public void resume(Object entity) throws IllegalStateException
   {
      if (entity == null)
      {
         sendResponse(Response.noContent().build());
      }
      else if (entity instanceof Response)
      {
         sendResponse((Response) entity);
      }
      else
      {
         if (method == null) throw new IllegalStateException("Unknown media type for response entity");
         MediaType type = method.resolveContentType(request, entity);
         sendResponse(Response.ok(entity, type).build());
      }
   }

   @Override
   public void resume(Throwable exc) throws IllegalStateException
   {
      dispatcher.handleException(request, response, exc);
   }






}
