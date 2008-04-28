package org.resteasy.test.finegrain.resource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedContainer;
import org.resteasy.test.MockDispatcherFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocatorTest
{

   private static Dispatcher dispatcher;


   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
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
      public String doGet(@PathParam("param")String param)
      {
         Assert.assertEquals("2", param);
         return this.getClass().getName() + "-" + param;
      }
   }


   public static class Subresource
   {

      @GET
      public String doGet()
      {
         return this.getClass().getName();
      }

      @Path("/subresource2")
      public Object getSubresource2()
      {
         return new Subresource2();
      }
   }

   @Path("/")
   public static class BaseResource
   {
      @Path("base/{param}/resources")
      public Object getSubresource(@PathParam("param")String param)
      {
         System.out.println("Here in BaseResource");
         Assert.assertEquals("1", param);
         return new Subresource();

      }
   }

   @Test
   public void testSubresource() throws Exception
   {

      HttpServletDispatcher servlet = MockDispatcherFactory.createDispatcher();
      Dispatcher dispatcher = servlet.getDispatcher();

      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/base/1/resources");
         request.setPathInfo("/base/1/resources");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Subresource.class.getName(), response.getContentAsString());
      }

      /*
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/base/1/resources");
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
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/base/1/resources/subresource2/stuff/2/bar");
         request.setPathInfo("/base/1/resources/subresource2/stuff/2/bar");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Subresource2.class.getName() + "-2", response.getContentAsString());
      }
   }

}