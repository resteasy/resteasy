package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocatorWithBaseExpressionTest
{

   private static Dispatcher dispatcher;


   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }


   public static class Subresource2
   {
      @GET
      @Path("stuff/{param}/bar")
      public String doGet(@PathParam("param") String param, @Context UriInfo uri)
      {
         System.out.println("Uri Ancesstors for Subresource2.doGet():");
         Assert.assertEquals(4, uri.getMatchedURIs().size());
         Assert.assertEquals("a1/base/1/resources/subresource2/stuff/2/bar", uri.getMatchedURIs().get(0));
         Assert.assertEquals("a1/base/1/resources/subresource2", uri.getMatchedURIs().get(1));
         Assert.assertEquals("a1/base/1/resources", uri.getMatchedURIs().get(2));
         Assert.assertEquals("a1", uri.getMatchedURIs().get(3));
         for (String ancestor : uri.getMatchedURIs()) System.out.println("   " + ancestor);


         System.out.println("Uri Ancesstors Object for Subresource2.doGet():");
         Assert.assertEquals(3, uri.getMatchedResources().size());
         Assert.assertEquals(Subresource2.class, uri.getMatchedResources().get(0).getClass());
         Assert.assertEquals(Subresource.class, uri.getMatchedResources().get(1).getClass());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(2).getClass());
         for (Object ancestor : uri.getMatchedResources()) System.out.println("   " + ancestor.getClass().getName());
         Assert.assertEquals("2", param);
         return this.getClass().getName() + "-" + param;
      }
   }


   public static class Subresource
   {

      @GET
      public String doGet(@Context UriInfo uri)
      {
         System.out.println("Uri Ancesstors for Subresource.doGet():");
         List<String> matchedURIs = uri.getMatchedURIs();
         Assert.assertEquals(2, matchedURIs.size());
         Assert.assertEquals("a1/base/1/resources", matchedURIs.get(0));
         Assert.assertEquals("a1", matchedURIs.get(1));
         for (String ancestor : matchedURIs) System.out.println("   " + ancestor);

         System.out.println("Uri Ancesstors Object for Subresource.doGet():");
         Assert.assertEquals(2, uri.getMatchedResources().size());
         Assert.assertEquals(Subresource.class, uri.getMatchedResources().get(0).getClass());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(1).getClass());
         for (Object ancestor : uri.getMatchedResources()) System.out.println("   " + ancestor.getClass().getName());
         return this.getClass().getName();
      }

      @Path("/subresource2")
      public Object getSubresource2(@Context UriInfo uri)
      {
         System.out.println("Uri Ancesstors for Subresource.getSubresource2():");
         Assert.assertEquals(3, uri.getMatchedURIs().size());
         Assert.assertEquals("a1/base/1/resources/subresource2", uri.getMatchedURIs().get(0));
         Assert.assertEquals("a1/base/1/resources", uri.getMatchedURIs().get(1));
         Assert.assertEquals("a1", uri.getMatchedURIs().get(2));
         for (String ancestor : uri.getMatchedURIs()) System.out.println("   " + ancestor);

         System.out.println("Uri Ancesstors Object for Subresource.getSubresource2():");
         Assert.assertEquals(2, uri.getMatchedResources().size());
         Assert.assertEquals(Subresource.class, uri.getMatchedResources().get(0).getClass());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(1).getClass());
         for (Object ancestor : uri.getMatchedResources()) System.out.println("   " + ancestor.getClass().getName());
         return new Subresource2();
      }
   }

   @Path("/a{x:\\d}")
   public static class BaseResource
   {
      @Path("base/{param}/resources")
      public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri)
      {
         System.out.println("Here in BaseResource");
         Assert.assertEquals("1", param);
         List<String> matchedURIs = uri.getMatchedURIs();
         Assert.assertEquals(2, matchedURIs.size());
         Assert.assertEquals("a1/base/1/resources", matchedURIs.get(0));
         Assert.assertEquals("a1", matchedURIs.get(1));
         for (String ancestor : matchedURIs) System.out.println("   " + ancestor);

         System.out.println("Uri Ancesstors Object for Subresource.doGet():");
         Assert.assertEquals(1, uri.getMatchedResources().size());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(0).getClass());

         return new Subresource();

      }
      @Path("proxy")
      public Subresource3Interface sub3()
      {

         return (Subresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{Subresource3Interface.class}, new InvocationHandler()
         {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
               return method.invoke(new Subresource3(), args);
            }
         });



         //return new Subresource3();
      }
   }

   public static interface Subresource3Interface
   {
      @GET
      @Path("3")
      String get(@QueryParam("foo") List<Double> params);
   }


   public static class Subresource3 implements Subresource3Interface
   {
      @Override
      public String get(List<Double> params)
      {
         Assert.assertNotNull(params);
         Assert.assertEquals(2, params.size());
         double p1 = params.get(0);
         double p2 = params.get(1);
         return "Subresource3";
      }
   }

   @Test
   public void testSubresource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
      {
         MockHttpRequest request = MockHttpRequest.get("/a1/base/1/resources");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Subresource.class.getName(), response.getContentAsString());
      }

      /*
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/base/1/resources");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         String response = method.getResponseBodyAsString();
         Assert.assertEquals(Subresource.class.getName(), response);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
      */

      {
         MockHttpRequest request = MockHttpRequest.get("/a1/base/1/resources/subresource2/stuff/2/bar");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Subresource2.class.getName() + "-2", response.getContentAsString());
      }
   }

}
