package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * RESTEASY-489
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PrivateConstructorTest extends BaseResourceTest
{

   @Path("/test")
   public static class MyService
   {
      MyService()
      {

      }

      public MyService(@Context javax.servlet.ServletContext context, @Context HttpServletRequest request)
      {

      }

      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello";
      }
   }

   @Before
   public void init() throws Exception
   {
      addPerRequestResource(MyService.class);
   }


   @Test
   public void testMapper() throws Exception
   {
      ClientRequest request = new ClientRequest(generateBaseUrl() + "/test");
      ClientResponse response = request.get();
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
   }


}
