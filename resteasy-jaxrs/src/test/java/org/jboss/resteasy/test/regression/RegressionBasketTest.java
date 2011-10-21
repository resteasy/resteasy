package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;

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

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(MyTest.class);
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
}
