package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * A basket of JIRA regression tests
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegressionBasketTest extends BaseResourceTest
{
   @Path("/inputstream")
   public static class MyTest
   {
      @POST
      @Path("/test/{type}")
      public void test(InputStream is, @PathParam("type") final String type) throws IOException
      {

      }


   }

   @Path("/{api:(?i:api)}")
   public static class Api
   {
      @Path("/{func:(?i:func)}")
      @GET
      @Produces("text/plain")
      public String func()
      {
         return "hello";
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(MyTest.class);
      addPerRequestResource(Api.class);
   }

   @Test
   public void test534() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/inputstream/test/json"));
      request.body(MediaType.APPLICATION_OCTET_STREAM, "hello world".getBytes());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void test624() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/ApI/FuNc"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();

   }
}
