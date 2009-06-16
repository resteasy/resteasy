package org.jboss.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
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
      HttpClient client = new HttpClient();
      HeadMethod method = new HeadMethod(TestPortProvider.generateURL("/GetTest"));
      method.addRequestHeader("Accept", "text/plain");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      Header header = method.getResponseHeader("CTS-HEAD");
      Assert.assertNotNull(header);
      Assert.assertEquals("text-plain", header.getValue());
   }

   /*
    * lient invokes HEAD on root resource at /GetTest;
    *                 which no request method designated for HEAD;
    *                 Verify that corresponding GET Method is invoked.
    */
   @Test
   public void testHead2() throws Exception
   {
      HttpClient client = new HttpClient();
      HeadMethod method = new HeadMethod(TestPortProvider.generateURL("/GetTest"));
      method.addRequestHeader("Accept", "text/html");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      Header header = method.getResponseHeader("CTS-HEAD");
      Assert.assertNotNull(header);
      Assert.assertEquals("text-html", header.getValue());
   }

   /*
    * Client invokes HEAD on sub resource at /GetTest/sub;
    * which no request method designated for HEAD;
    * Verify that corresponding GET Method is invoked instead.
    */
   @Test
   public void testHeadSubresource() throws Exception
   {
      HttpClient client = new HttpClient();
      HeadMethod method = new HeadMethod(TestPortProvider.generateURL("/GetTest/sub"));
      method.addRequestHeader("Accept", "text/plain");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      Header header = method.getResponseHeader("CTS-HEAD");
      Assert.assertNotNull(header);
      Assert.assertEquals("sub-text-plain", header.getValue());
   }

   /*
    * If client invokes OPTIONS and there is no request method that exists, verify that an automatic response is
    * generated
    */
   @Test
   public void testOptions() throws Exception
   {
      HttpClient client = new HttpClient();
      OptionsMethod method = new OptionsMethod(TestPortProvider.generateURL("/GetTest/sub"));
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      Header allowHeader = method.getResponseHeader("Allow");
      Assert.assertNotNull(allowHeader);
      String allowValue = allowHeader.getValue();
      String[] allowed = allowValue.split(",");
      HashSet set = new HashSet();
      for (String allow : allowed)
      {
         set.add(allow.trim());
      }

      Assert.assertTrue(set.contains("GET"));
      Assert.assertTrue(set.contains("OPTIONS"));
      Assert.assertTrue(set.contains("HEAD"));

   }


}
