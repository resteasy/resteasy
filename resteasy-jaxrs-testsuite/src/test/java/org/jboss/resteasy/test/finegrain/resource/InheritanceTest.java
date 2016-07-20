package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

/**
 * Spec requires that HEAD and OPTIONS are handled in a default manner
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InheritanceTest extends BaseResourceTest
{
   private static Client client;

   public static interface ParentResource
   {

      @GET
      @Produces("text/plain")
      public String firstest();

   }

   @Path(value = "/InheritanceTest")
   public static class ChildResource implements ParentResource
   {

      public String firstest()
      {
         return "First";
      }
   }

   public static interface ParentResource1
   {

      @GET
      @Produces("text/xml")
      public String secondtest();
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

   @BeforeClass
   public static void beforeSub()
   {
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterSub()
   {
      client.close();
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
      Builder builder = client.target(TestPortProvider.generateURL("/InheritanceTest")).request();
      builder.header("Accept", "text/plain");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("First", response.readEntity(String.class));
   }


   /*
    *  Client sends a request on a resource at /InheritanceTest1,
    *                 Verify that inheritance works.
    */
   @Test
   public void Test2() throws Exception
   {
      Builder builder = client.target(TestPortProvider.generateURL("/InheritanceTest1")).request();
      builder.header("Accept", "text/html");
      Response response = builder.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertTrue(response.readEntity(String.class).indexOf("Second") > -1);
   }


}