package com.restfully.shop.test;

import org.junit.Test;

import java.io.FileNotFoundException;
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
      // Show the update
      System.out.println("**** Get Unknown Customer ***");
      URL getUrl = new URL("http://localhost:9095/customers/1");
      HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      try
      {
         int code = connection.getResponseCode();
      }
      catch (FileNotFoundException e)
      {
         System.out.println("Customer not found.");
      }
      connection.disconnect();
   }
}
