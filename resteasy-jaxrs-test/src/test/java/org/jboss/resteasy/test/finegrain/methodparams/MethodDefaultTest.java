package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashSet;

/**
 * Spec requires that HEAD and OPTIONS are handled in a default manner
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MethodDefaultTest extends BaseResourceTest
{

   @Path(value = "/GetTest")
   public static class Resource
   {

      static String html_content =
              "<html>" + "<head><title>CTS-get text/html</title></head>" +
                      "<body>CTS-get text/html</body></html>";

      @GET
      public Response getPlain()
      {
         return Response.ok("CTS-get text/plain").header("CTS-HEAD", "text-plain").
                 build();
      }

      @GET
      @Produces(value = "text/html")
      public Response getHtml()
      {
         return Response.ok(html_content).header("CTS-HEAD", "text-html").
                 build();
      }

      @GET
      @Path(value = "/sub")
      public Response getSub()
      {
         return Response.ok("CTS-get text/plain").header("CTS-HEAD",
                 "sub-text-plain").
                 build();
      }

      @GET
      @Path(value = "/sub")
      @Produces(value = "text/html")
      public Response headSub()
      {
         return Response.ok(html_content).header("CTS-HEAD", "sub-text-html").
                 build();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      deployment.getRegistry().addPerRequestResource(Resource.class);
   }

   /*
    * Client invokes Head on root resource at /GetTest;
    *                 which no request method designated for HEAD;
    *                 Verify that corresponding GET Method is invoked.
    */
   @Test
   public void testHead() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/GetTest"));
      request.header("Accept", "text/plain");
      ClientResponse<?> response = request.head();
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaders().getFirst("CTS-HEAD");
      Assert.assertEquals("text-plain", header);
      response.releaseConnection();
   }

   /*
    * Client invokes HEAD on root resource at /GetTest;
    *                 which no request method designated for HEAD;
    *                 Verify that corresponding GET Method is invoked.
    */
   @Test
   public void testHead2() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/GetTest"));
      request.header("Accept", "text/html");
      ClientResponse<?> response = request.head();
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaders().getFirst("CTS-HEAD");
      Assert.assertEquals("text-html", header);
      response.releaseConnection();
   }

   /*
    * Client invokes HEAD on sub resource at /GetTest/sub;
    * which no request method designated for HEAD;
    * Verify that corresponding GET Method is invoked instead.
    */
   @Test
   public void testHeadSubresource() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/GetTest/sub"));
      request.header("Accept", "text/plain");
      ClientResponse<?> response = request.head();
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaders().getFirst("CTS-HEAD");
      Assert.assertEquals("sub-text-plain", header);
      response.releaseConnection();
   }

   /*
    * If client invokes OPTIONS and there is no request method that exists, verify that an automatic response is
    * generated
    */
   @Test
   public void testOptions() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/GetTest/sub"));
      ClientResponse<?> response = request.options();
      Assert.assertEquals(200, response.getStatus());
      String allowedHeader = response.getHeaders().getFirst("Allow");
      Assert.assertNotNull(allowedHeader);
      String[] allowed = allowedHeader.split(",");
      HashSet<String> set = new HashSet<String>();
      for (String allow : allowed)
      {
         set.add(allow.trim());
      }

      Assert.assertTrue(set.contains("GET"));
      Assert.assertTrue(set.contains("OPTIONS"));
      Assert.assertTrue(set.contains("HEAD"));
      response.releaseConnection();
   }

}
