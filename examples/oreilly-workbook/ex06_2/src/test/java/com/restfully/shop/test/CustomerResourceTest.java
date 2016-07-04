package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
      Customer cust = new Customer();
      cust.setFirstName("Bill");
      cust.setLastName("Burke");
      cust.setStreet("256 Clarendon Street");
      cust.setCity("Boston");
      cust.setState("MA");
      cust.setZip("02115");
      cust.setCountry("USA");

      URL postUrl = new URL("http://localhost:9095/customers");
      HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-java-serialized-object");
      OutputStream os = connection.getOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(os);
      oos.writeObject(cust);
      oos.flush();
      Assert.assertEquals(HttpURLConnection.HTTP_CREATED, connection.getResponseCode());
      System.out.println("Location: " + connection.getHeaderField("Location"));
      connection.disconnect();


      // Get the new customer
      System.out.println("*** GET Created Customer **");
      URL getUrl = new URL("http://localhost:9095/customers/1");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");
      System.out.println("Content-Type: " + connection.getContentType());

      ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
      cust = (Customer) ois.readObject();
      System.out.println(cust);
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      connection.disconnect();

      // Update the new customer.  Change Bill's name to William
      cust.setFirstName("William");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("PUT");
      connection.setRequestProperty("Content-Type", "application/x-java-serialized-object");
      os = connection.getOutputStream();
      oos = new ObjectOutputStream(os);
      oos.writeObject(cust);
      oos.flush();
      Assert.assertEquals(HttpURLConnection.HTTP_NO_CONTENT, connection.getResponseCode());
      connection.disconnect();

      // Show the update
      System.out.println("**** After Update ***");
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");

      System.out.println("Content-Type: " + connection.getContentType());
      Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
      ois = new ObjectInputStream(connection.getInputStream());
      cust = (Customer) ois.readObject();
      System.out.println(cust);
      connection.disconnect();
   }
}
