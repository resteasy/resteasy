package org.jboss.resteasy.spi;

import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.MessageBodyParameterInjector;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

public class InternalDispatch
{
   private static InternalDispatch instance = new InternalDispatch();

   // using a singleton so that that end users can override this behavior
   public static InternalDispatch getInstance()
   {
      return instance;
   }

   public static void setInstance(InternalDispatch instance)
   {
      InternalDispatch.instance = instance;
   }

   public Object getEntity(String uri)
   {
      return getResponse(uri).getEntity();
   }

   public Response getResponse(String uri)
   {
      return getResponse(createRequest(uri, "GET"));
   }

   public Response delete(String uri)
   {
      return getResponse(createRequest(uri, "DELETE"));
   }

   public Response putEntity(String uri, Object entity)
   {
      return messageBodyRequest(uri, entity, "PUT");
   }

   public Response postEntity(String uri, Object entity)
   {
      return messageBodyRequest(uri, entity, "POST");
   }

   protected Response messageBodyRequest(String uri, Object entity,
         String verb)
   {
      MockHttpRequest request = createRequest(uri, verb);
      if( entity != null )
         MessageBodyParameterInjector.pushBody(entity);
      try
      {
         return getResponse(request);
      }
      finally
      {
         if( entity != null )
            MessageBodyParameterInjector.popBody();
      }
   }

   public Response getResponse(MockHttpRequest request)
   {
      Dispatcher dispatcher = ResteasyProviderFactory.getContextData(Dispatcher.class);
      if (dispatcher == null) return null;
      enhanceRequest(request);
      return dispatcher.internalInvocation(request, new MockHttpResponse());
   }

   protected void enhanceRequest(MockHttpRequest request)
   {
      HttpRequest previousRequest = ResteasyProviderFactory.getContextData(HttpRequest.class);
      if (previousRequest != null)
      {
         request.getHttpHeaders().getRequestHeaders().putAll(
               previousRequest.getHttpHeaders().getRequestHeaders());
      }
   }

   protected MockHttpRequest createRequest(String uri, String verb)
   {
      try
      {
         return MockHttpRequest.create(verb, uri);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException("could not create uri for internal dispatching", e);
      }
   }
}
