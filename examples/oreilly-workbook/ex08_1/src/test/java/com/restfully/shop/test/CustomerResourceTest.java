package com.restfully.shop.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
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

      URL postUrl = new URL("http://localhost:9095/customers");
      HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/xml");
      OutputStream os = connection.getOutputStream();
      os.write(newCustomer.getBytes());
      os.flush();
      Assert.assertEquals(HttpURLConnection.HTTP_CREATED, connection.getResponseCode());
      System.out.println("Location: " + connection.getHeaderField("Location"));
      connection.disconnect();


      // Get XML customer
      System.out.println("*** GET Customer as XML **");
      URL getUrl = new URL("http://localhost:9095/customers/1");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/xml");
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      Assert.assertEquals("application/xml", connection.getContentType());

      BufferedReader reader = new BufferedReader(new
              InputStreamReader(connection.getInputStream()));

      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
      connection.disconnect();


      // Get json
      System.out.println("*** GET Customer as JSON **");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/json");
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      Assert.assertEquals("application/json", connection.getContentType());

      reader = new BufferedReader(new
              InputStreamReader(connection.getInputStream()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
      connection.disconnect();

      // Get json
      System.out.println("*** GET Customer as plain text **");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "text/plain");
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      Assert.assertEquals("text/plain", connection.getContentType());

      reader = new BufferedReader(new
              InputStreamReader(connection.getInputStream()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
      connection.disconnect();

   }
}
