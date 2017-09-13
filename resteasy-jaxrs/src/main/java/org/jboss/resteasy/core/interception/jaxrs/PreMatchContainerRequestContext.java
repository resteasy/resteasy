package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreMatchContainerRequestContext implements ContainerRequestContext
{
   protected final HttpRequest httpRequest;
   protected Response response;
   private ContainerRequestFilter[] requestFilters;
   private int currentFilter;
   private boolean suspended;
   private boolean filterReturnIsMeaningful = true;
   private Runnable continuation;
   private Map<Class<?>, Object> contextDataMap;

   public PreMatchContainerRequestContext(HttpRequest request, 
         ContainerRequestFilter[] requestFilters, Runnable continuation)
   {
      this.httpRequest = request;
      this.requestFilters = requestFilters;
      this.continuation = continuation;
      contextDataMap = ResteasyProviderFactory.getContextDataMap();
   }

   public HttpRequest getHttpRequest()
   {
      return httpRequest;
   }

   public Response getResponseAbortedWith()
   {
      return response;
   }

   @Override
   public Object getProperty(String name)
   {
      return httpRequest.getAttribute(name);
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      ArrayList<String> names = new ArrayList<String>();
      Enumeration<String> enames = httpRequest.getAttributeNames();
      while (enames.hasMoreElements())
      {
         names.add(enames.nextElement());
      }
      return names;
   }

   @Override
   public void setProperty(String name, Object object)
   {
      httpRequest.setAttribute(name, object);
   }

   @Override
   public void removeProperty(String name)
   {
      httpRequest.removeAttribute(name);
   }

   @Override
   public UriInfo getUriInfo()
   {
      return httpRequest.getUri();
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      httpRequest.setRequestUri(requestUri);
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      httpRequest.setRequestUri(baseUri, requestUri);
   }

   @Override
   public String getMethod()
   {
      return httpRequest.getHttpMethod();
   }

   @Override
   public void setMethod(String method)
   {
      httpRequest.setHttpMethod(method);
   }

   @Override
   public MultivaluedMap<String, String> getHeaders()
   {
      return httpRequest.getHttpHeaders().getRequestHeaders();
   }

   @Override
   public Date getDate()
   {
      return httpRequest.getHttpHeaders().getDate();
   }

   @Override
   public Locale getLanguage()
   {
      return httpRequest.getHttpHeaders().getLanguage();
   }

   @Override
   public int getLength()
   {
      return httpRequest.getHttpHeaders().getLength();
   }

   @Override
   public MediaType getMediaType()
   {
      return httpRequest.getHttpHeaders().getMediaType();
   }

   @Override
   public List<MediaType> getAcceptableMediaTypes()
   {
      return httpRequest.getHttpHeaders().getAcceptableMediaTypes();
   }

   @Override
   public List<Locale> getAcceptableLanguages()
   {
      return httpRequest.getHttpHeaders().getAcceptableLanguages();
   }

   @Override
   public Map<String, Cookie> getCookies()
   {
      return httpRequest.getHttpHeaders().getCookies();
   }

   @Override
   public boolean hasEntity()
   {
      return getMediaType() != null;
   }

   @Override
   public InputStream getEntityStream()
   {
      return httpRequest.getInputStream();
   }

   @Override
   public void setEntityStream(InputStream entityStream)
   {
      httpRequest.setInputStream(entityStream);
   }

   @Override
   public SecurityContext getSecurityContext()
   {
      return ResteasyProviderFactory.getContextData(SecurityContext.class);
   }

   @Override
   public void setSecurityContext(SecurityContext context)
   {
      ResteasyProviderFactory.pushContext(SecurityContext.class, context);
   }

   @Override
   public Request getRequest()
   {
      return ResteasyProviderFactory.getContextData(Request.class);
   }

   @Override
   public String getHeaderString(String name)
   {
      return httpRequest.getHttpHeaders().getHeaderString(name);
   }

   public synchronized void suspend() {
      if(continuation == null)
         throw new RuntimeException("Suspend not supported yet");
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
      {
         this.response = response;
      }
   }
   
   public synchronized void resume() {
      if(!suspended)
         throw new RuntimeException("Cannot resume: not suspended");
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
            filterReturnIsMeaningful = false;
            response = null;
            return null;
         }
         BuiltResponse serverResponse = (BuiltResponse)getResponseAbortedWith();
         if (serverResponse != null)
         {
            // handle the case where we've been suspended by a previous filter
            if(filterReturnIsMeaningful)
               return serverResponse;
            else
               httpRequest.getAsyncContext().getAsyncResponse().resume(serverResponse);
         }
      }
      // here it means we reached the last filter
      // if we've never been suspended, the caller is valid and let it go on doing the request
      if(filterReturnIsMeaningful)
         return null;
      // if we've been suspended then the caller is a filter and have to invoke our continuation
      continuation.run();
      return null;
   }

   public boolean isSuspended()
   {
      return !filterReturnIsMeaningful;
   }
}
