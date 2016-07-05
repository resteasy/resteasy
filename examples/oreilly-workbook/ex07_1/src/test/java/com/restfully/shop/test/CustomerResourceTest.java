package com.restfully.shop.test;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * This example has changed from the description in the book.  Previously, the error body was not being shown.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
   @Test
   public void testCustomerResource() throws Exception
   {
      // Show the update
      System.out.println("**** Get Unknown Customer ***");
      URL getUrl = new URL("http://localhost:9095/customers/1");
      HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      int code = connection.getResponseCode();

      // Print out the error message

      System.out.println(code + " " + connection.getResponseMessage());
      BufferedReader reader = new BufferedReader(new
              InputStreamReader(connection.getErrorStream()));

      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
   }
}
