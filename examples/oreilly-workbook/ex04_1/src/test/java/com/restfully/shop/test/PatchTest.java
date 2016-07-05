package com.restfully.shop.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PatchTest
{
   private static class HttpPatch extends HttpPost
   {
      public HttpPatch(String s)
      {
         super(s);
      }

      public String getMethod()
      {
         return "PATCH";
      }
   }

   @Test
   public void testCustomerResource() throws Exception
   {
      System.out.println("*** Create a new Customer ***");
      // Create a new customer
      String newCustomer = "<customer>"
              + "<first-name>Bill</first-name>"
              + "<last-name>Burke</last-name>"
              + "<street>256 Clarendon Street</street>"
              + "<city>Boston</city>"
              + "<state>MA</state>"
              + "<zip>02115</zip>"
              + "<country>USA</country>"
              + "</customer>";

      DefaultHttpClient client = new DefaultHttpClient();

      HttpPost post = new HttpPost("http://localhost:9095/customers");
      StringEntity entity = new StringEntity(newCustomer);
      entity.setContentType("application/xml");
      post.setEntity(entity);
      HttpClientParams.setRedirecting(post.getParams(), false);
      HttpResponse response = client.execute(post);

      Assert.assertEquals(201, response.getStatusLine().getStatusCode());
      System.out.println("Location: " + response.getLastHeader("Location"));

      HttpPatch patch = new HttpPatch("http://localhost:9095/customers/1");

      // Update the new customer.  Change Bill's name to William
      String patchCustomer = "<customer>"
              + "<first-name>William</first-name>"
              + "</customer>";
      entity = new StringEntity(patchCustomer);
      entity.setContentType("application/xml");
      patch.setEntity(entity);
      response = client.execute(patch);

      Assert.assertEquals(204, response.getStatusLine().getStatusCode());

      // Show the update
      System.out.println("**** After Update ***");
      HttpGet get = new HttpGet("http://localhost:9095/customers/1");
      response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());

      System.out.println("Content-Type: " + response.getEntity().getContentType());
      BufferedReader reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
   }
}
