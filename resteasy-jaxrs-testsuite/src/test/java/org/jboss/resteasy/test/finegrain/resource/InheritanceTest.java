package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Spec requires that HEAD and OPTIONS are handled in a default manner
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InheritanceTest extends BaseResourceTest
{


   public interface ParentResource
   {

      @GET
      @Produces("text/plain")
      String firstest();

   }

   @Path(value = "/InheritanceTest")
   public static class ChildResource implements ParentResource
   {

      public String firstest()
      {
         return "First";
      }
   }

   public interface ParentResource1
   {

      @GET
      @Produces("text/xml")
      String secondtest();
   }


   @Path(value = "/InheritanceTest1")
   public static class ChildResource1 implements ParentResource1
   {
      static String html_content =
              "<html>" + "<head><title>JAX-RS Test</title></head>" +
                      "<body>Second</body></html>";

      @GET
      @Produces("text/html")
      public String secondtest()
      {
         return html_content;
      }
   }


   @Before
   public void setUp() throws Exception
   {
      deployment.getRegistry().addPerRequestResource(ChildResource.class);
      deployment.getRegistry().addPerRequestResource(ChildResource1.class);
   }

   /*
    *
    * Client sends a request on a resource at /InheritanceTest,
    *                 Verify that inheritance works.
    */
   @Test
   public void Test1() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/InheritanceTest"));
      request.header("Accept", "text/plain");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("First", response.getEntity());
   }


   /*
    *  Client sends a request on a resource at /InheritanceTest1,
    *                 Verify that inheritance works.
    */
   @Test
   public void Test2() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/InheritanceTest1"));
      request.header("Accept", "text/html");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertTrue(response.getEntity().indexOf("Second") > -1);
   }


}