package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieTest extends BaseResourceTest
{
   @Path("cookie")
   public static class Resource
   {
      @GET
      @Produces("text/plain")
      public Response responseOk()
      {
         return Response.ok("ok").header("Set-Cookie", "guid=1.9112608617070927872;Path=/;Domain=localhost;Expires=Thu, 03-May-2018 10:36:34 GMT;Max-Age=150000000").build();
      }

   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testWeirdCookie()
   {
      Response response = client.target(generateURL("/cookie")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      Map<String,NewCookie> cookies = response.getCookies();
      for (String key : cookies.keySet())
      {
         System.out.println("[" + key + "] >>>>>> " + cookies.get(key) + "");
      }
      for (String header : response.getStringHeaders().keySet()) {
         System.out.println("header: " + header);
         List<String> values = response.getStringHeaders().get(header);
         for (String val : values) {
            System.out.println("    " + val);
         }
      }
       Assert.assertTrue(cookies.containsKey("guid"));
      response.close();
   }

}
