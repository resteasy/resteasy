package com.restfully.shop.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InjectionTest
{
   @Test
   public void testCarResource() throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();

      System.out.println("**** CarResource Via @MatrixParam ***");
      HttpGet get = new HttpGet("http://localhost:8080/ex05_1/services/cars/matrix/mercedes/e55;color=black/2006");
      HttpResponse response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      BufferedReader reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }


      System.out.println("**** CarResource Via PathSegment ***");
      get = new HttpGet("http://localhost:8080/ex05_1/services/cars/segment/mercedes/e55;color=black/2006");
      response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }

      System.out.println("**** CarResource Via PathSegments ***");
      get = new HttpGet("http://localhost:8080/ex05_1/services/cars/segments/mercedes/e55/amg/year/2006");
      response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }

      System.out.println("**** CarResource Via PathSegment ***");
      get = new HttpGet("http://localhost:8080/ex05_1/services/cars/uriinfo/mercedes/e55;color=black/2006");
      response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
      System.out.println();
      System.out.println();

   }

   @Test
   public void testCustomerResource() throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();

      System.out.println("**** CustomerResource No Query params ***");
      HttpGet get = new HttpGet("http://localhost:8080/ex05_1/services/customers");
      HttpResponse response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      BufferedReader reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }

      System.out.println("**** CustomerResource With Query params ***");
      get = new HttpGet("http://localhost:8080/ex05_1/services/customers?start=1&size=3");
      response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }

      System.out.println("**** CustomerResource With UriInfo and Query params ***");
      get = new HttpGet("http://localhost:8080/ex05_1/services/customers/uriinfo?start=2&size=2");
      response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      reader = new BufferedReader(new
              InputStreamReader(response.getEntity().getContent()));

      line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
   }
}
