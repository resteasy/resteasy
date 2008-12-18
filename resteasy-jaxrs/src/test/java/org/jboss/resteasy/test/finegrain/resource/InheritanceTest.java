package org.jboss.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
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


   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(ChildResource.class);
      dispatcher.getRegistry().addPerRequestResource(ChildResource1.class);
   }

   /*
    *
    * Client sends a request on a resource at /InheritanceTest,
    *                 Verify that inheritance works.
    */
   @Test
   public void Test1() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TestPortProvider.generateURL("/InheritanceTest"));
      method.addRequestHeader("Accept", "text/plain");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      Assert.assertEquals("First", method.getResponseBodyAsString());
   }


   /*
    *  Client sends a request on a resource at /InheritanceTest1,
    *                 Verify that inheritance works.
    */
   @Test
   public void Test2() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TestPortProvider.generateURL("/InheritanceTest1"));
      method.addRequestHeader("Accept", "text/html");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      Assert.assertTrue(method.getResponseBodyAsString().indexOf("Second") > -1);

   }


}