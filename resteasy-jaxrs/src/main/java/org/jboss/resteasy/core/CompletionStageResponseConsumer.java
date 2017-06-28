package org.jboss.resteasy.core;

import java.util.Map;
import java.util.function.BiConsumer;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class CompletionStageResponseConsumer implements BiConsumer<Object, Throwable>
{
   private Map<Class<?>, Object> contextDataMap;
   private ResourceMethodInvoker method;
   private SynchronousDispatcher dispatcher;
   private AsyncContext asyncContext;
   private boolean isComplete;

   public CompletionStageResponseConsumer(ResourceMethodInvoker method)
   {
      this.method = method;
      contextDataMap = ResteasyProviderFactory.getContextDataMap();
      dispatcher = (SynchronousDispatcher) contextDataMap.get(Dispatcher.class);
      HttpServletRequest httpServletRequest = (HttpServletRequest) contextDataMap.get(HttpServletRequest.class);
      asyncContext = httpServletRequest.startAsync();
   }

   @Override
   public void accept(Object t, Throwable u)
   {
      try
      {
         if (t != null || u == null)
         {
            internalResume(t);
         }
         else
         {
            internalResume(u);
         }
      }
      finally
      {
         complete();
      }
   }

   synchronized public void complete()
   {
      if (!isComplete)
      {
         isComplete = true;
         asyncContext.complete();
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }

   protected void internalResume(Object entity)
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      HttpRequest httpRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
      HttpResponse httpResponse = (HttpResponse) contextDataMap.get(HttpResponse.class);

      try
      {
         BuiltResponse builtResponse = createResponse(entity, httpRequest);
         ServerResponseWriter.writeNomapResponse(builtResponse, httpRequest, httpResponse, dispatcher.getProviderFactory());
      }
      catch (Throwable e)
      {
         internalResume(e);
      }
   }

   protected void internalResume(Throwable t)
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      HttpRequest httpRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
      HttpResponse httpResponse = (HttpResponse) contextDataMap.get(HttpResponse.class);
      dispatcher.writeException(httpRequest, httpResponse, t);
   }

   protected BuiltResponse createResponse(Object entity, HttpRequest httpRequest)
   {
      BuiltResponse builtResponse = null;
      if (entity == null)
      {
         builtResponse = (BuiltResponse) Response.noContent().build();
      }
      else if (entity instanceof BuiltResponse)
      {
         builtResponse = (BuiltResponse) entity;
      }
      else if (entity instanceof Response)
      {
         Response r = (Response) entity;
         Headers<Object> metadata = new Headers<Object>();
         metadata.putAll(r.getMetadata());
         builtResponse = new BuiltResponse(r.getStatus(), r.getStatusInfo().getReasonPhrase(), metadata, r.getEntity(),  method.getMethodAnnotations());
      }
      else
      {
         if (method == null)
         {
            throw new IllegalStateException(Messages.MESSAGES.unknownMediaTypeResponseEntity());
         }
         BuiltResponse jaxrsResponse = (BuiltResponse) Response.ok(entity).build();
         jaxrsResponse.setGenericType(method.getGenericReturnType());
         jaxrsResponse.addMethodAnnotations(method.getMethodAnnotations());
         builtResponse = jaxrsResponse;
      }

      return builtResponse;
   }
}
