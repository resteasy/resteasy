package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.util.Iterator;
import java.util.List;

public class DispatcherUtilities
{

   private ResteasyProviderFactory providerFactory;
   private Registry registry;

   public DispatcherUtilities(ResteasyProviderFactory providerFactory, Registry registry)
   {
      super();
      this.providerFactory = providerFactory;
      this.registry = registry;
   }

   public MediaType resolveContentType(Response jaxrsResponse)
   {
      MediaType responseContentType = null;
      Object type = jaxrsResponse.getMetadata().getFirst(
              HttpHeaderNames.CONTENT_TYPE);
      if (type == null)
         return MediaType.valueOf("*/*");
      if (type instanceof MediaType)
      {
         responseContentType = (MediaType) type;
      }
      else
      {
         responseContentType = MediaType.valueOf(type.toString());
      }
      return responseContentType;
   }

   public void outputHeaders(HttpResponse response, Response jaxrsResponse)
   {
      if (jaxrsResponse.getMetadata() != null
              && jaxrsResponse.getMetadata().size() > 0)
      {
         response.getOutputHeaders().putAll(jaxrsResponse.getMetadata());
      }
   }

   public void writeCookies(HttpResponse response, Response jaxrsResponse)
   {
      if (jaxrsResponse.getMetadata() != null)
      {
         List<Object> cookies = jaxrsResponse.getMetadata().get(
                 HttpHeaderNames.SET_COOKIE);
         if (cookies != null)
         {
            Iterator<Object> it = cookies.iterator();
            while (it.hasNext())
            {
               Object next = it.next();
               if (next instanceof NewCookie)
               {
                  NewCookie cookie = (NewCookie) next;
                  response.addNewCookie(cookie);
                  it.remove();
               }
            }
            if (cookies.size() < 1)
               jaxrsResponse.getMetadata().remove(HttpHeaderNames.SET_COOKIE);
         }
      }
   }

   public void pushContextObjects(HttpRequest request, HttpResponse response)
   {
      ResteasyProviderFactory.pushContext(HttpRequest.class, request);
      ResteasyProviderFactory.pushContext(HttpResponse.class, response);
      ResteasyProviderFactory.pushContext(HttpHeaders.class, request.getHttpHeaders());
      ResteasyProviderFactory.pushContext(UriInfo.class, request.getUri());
      ResteasyProviderFactory.pushContext(Request.class, new RequestImpl(request));
      ResteasyProviderFactory.pushContext(Providers.class, providerFactory);
      ResteasyProviderFactory.pushContext(Registry.class, registry);
   }

   public void clearContextData()
   {
      ResteasyProviderFactory.clearContextData();
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }
}
