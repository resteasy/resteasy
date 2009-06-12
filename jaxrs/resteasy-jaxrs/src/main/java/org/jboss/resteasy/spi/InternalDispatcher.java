package org.jboss.resteasy.spi;

import static org.jboss.resteasy.spi.ResteasyProviderFactory.getContextData;

import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

/**
 * <p>
 * InternalDispatch represents a "forward" in servlet terms. You can perform an
 * internal GET/POST/DELETE/PUT using IntenalDispatch using Java Object. For
 * example:
 * </p>
 * 
 * 
 * <pre>
 * &#064;GET
 * &#064;Produces(&quot;text/plain&quot;)
 * &#064;Path(&quot;/forward/object&quot;)
 * public SomeObject forward(@Context InternalDispatcher dispatcher)
 * {
 *    return (SomeObject) dispatcher.getEntity(&quot;/some-object&quot;);
 * }
 * </pre>
 * 
 * <p>
 * That previous snippet performs an internal request to /some-object and
 * returns the Object representation of the Resource that lives at
 * "/some-object".
 * </p>
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class InternalDispatcher
{

   private static InternalDispatcher instance = new InternalDispatcher();

   // using a singleton so that that end users can override this behavior
   public static InternalDispatcher getInstance()
   {
      return instance;
   }

   public static void setInstance(InternalDispatcher instance)
   {
      InternalDispatcher.instance = instance;
   }
   
   public Object getEntity(String uri)
   {
      return getResponse(uri).getEntity();
   }

   public Response delete(String uri)
   {
      return getResponse(createRequest(uri, "DELETE"));
   }

   public Response putEntity(String uri, Object entity)
   {
      return getResponse(createRequest(uri, "PUT"), entity);
   }

   public Response postEntity(String uri, Object entity)
   {
      return getResponse(createRequest(uri, "POST"), entity);
   }

   public Response getResponse(String uri)
   {
      return getResponse(createRequest(uri, "GET"));
   }

   public Response getResponse(MockHttpRequest request)
   {
      return getResponse(request, null);
   }

   public Response getResponse(MockHttpRequest request, Object entity)
   {
      try
      {
         Dispatcher dispatcher = getContextData(Dispatcher.class);
         if (dispatcher == null)
         {
            return null;
         }
         enhanceRequest(request);
         return dispatcher.internalInvocation(request, new MockHttpResponse(), entity);
      }
      finally
      {
      }

   }

   protected void enhanceRequest(MockHttpRequest request)
   {
      HttpRequest previousRequest = getContextData(HttpRequest.class);
      if (previousRequest != null)
      {
         getHeaders(request).putAll(getHeaders(previousRequest));
      }
   }

   private MultivaluedMap<String, String> getHeaders(HttpRequest request)
   {
      return request.getHttpHeaders().getRequestHeaders();
   }

   public static MockHttpRequest createRequest(String uri, String verb)
   {
      try
      {
         return MockHttpRequest.create(verb, uri);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(
               "could not create uri for internal dispatching", e);
      }
   }
}
