package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMatchContainerRequestContext extends PreMatchContainerRequestContext
{
   protected final ResourceMethodInvoker resourceMethod;
   private ContainerRequestFilter[] requestFilters;
   private int currentFilter;
   private boolean suspended;
   private Runnable continuation;
   private Map<Class<?>, Object> contextDataMap;

   public PostMatchContainerRequestContext(HttpRequest request,ResourceMethodInvoker resourceMethod, 
         ContainerRequestFilter[] requestFilters, Runnable continuation)
   {
      super(request);
      this.resourceMethod = resourceMethod;
      this.requestFilters = requestFilters;
      this.continuation = continuation;
      contextDataMap = ResteasyProviderFactory.getContextDataMap();
   }

   public ResourceMethodInvoker getResourceMethod()
   {
      return resourceMethod;
   }

   @Override
   public void setMethod(String method)
   {
      throw new IllegalStateException(Messages.MESSAGES.cantSetMethod());
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException(Messages.MESSAGES.cantSetURI());
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException(Messages.MESSAGES.cantSetURI());
   }

   public synchronized void suspend() {
      suspended = true;
   }
   
   @Override
   public synchronized void abortWith(Response response)
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      if(suspended)
      {
         httpRequest.getAsyncContext().getAsyncResponse().resume(response);
      }
      else
         super.abortWith(response);
   }
   
   public synchronized void resume() {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      // just go on
      filter();
   }
   
   public synchronized BuiltResponse filter()
   {
      // FIXME: check what happens if the filter suspends and resumes/abort within the same call (same thread)
      while(currentFilter < requestFilters.length)
      {
         ContainerRequestFilter filter = requestFilters[currentFilter++];
         try
         {
            suspended = false;
            filter.filter(this);
         }
         catch (IOException e)
         {
            throw new ApplicationException(e);
         }
         if(suspended) {
            if(!httpRequest.getAsyncContext().isSuspended())
               httpRequest.getAsyncContext().suspend();
            // ignore any abort request until we are resumed
            response = null;
            return null;
         }
         BuiltResponse serverResponse = (BuiltResponse)getResponseAbortedWith();
         if (serverResponse != null)
         {
            // handle the case where we've been suspended by a previous filter
            // FIXME: when pre-filters can also suspend this is likely to just be wrong
            if(!httpRequest.getAsyncContext().isSuspended())
               return serverResponse;
            else
               httpRequest.getAsyncContext().getAsyncResponse().resume(serverResponse);
         }
      }
      // here it means we reached the last filter
      // if we've never been suspended, the caller is valid and let it go on doing the request
      // FIXME: when pre-filters can also suspend this is likely to just be wrong
      if(!httpRequest.getAsyncContext().isSuspended())
         return null;
      // if we've been suspended then the caller is a filter and have to invoke our continuation
      continuation.run();
      return null;
   }

   public boolean isSuspended()
   {
      // FIXME: when pre-filters can also suspend this is likely to just be wrong
      return httpRequest.getAsyncContext().isSuspended();
   }
}
