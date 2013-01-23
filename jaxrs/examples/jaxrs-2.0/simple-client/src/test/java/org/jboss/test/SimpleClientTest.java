package org.jboss.test;

import junit.framework.Assert;
import org.jboss.example.jaxrs2.async.Customer;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientException;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleClientTest
{
   @Test
   public void testResponse() throws Exception
   {
      // fill out a query param and execute a get request
      Client client = ClientFactory.newClient();
      WebTarget target = client.target("http://localhost:9095/customers");
      Response response = target.queryParam("name", "Bill").request().get();
      try
      {
         Assert.assertEquals(200, response.getStatus());
         Customer cust = response.readEntity(Customer.class);
         Assert.assertEquals("Bill", cust.getName());
      }
      finally
      {
         response.close();
         client.close();
      }
   }
   @Test
   public void testCustomer() throws Exception
   {
      // fill out a query param and execute a get request
      Client client = ClientFactory.newClient();
      WebTarget target = client.target("http://localhost:9095/customers");
      try
      {
         // extract customer directly expecting success
         Customer cust = target.queryParam("name", "Bill").request().get(Customer.class);
         Assert.assertEquals("Bill", cust.getName());
      }
      finally
      {
         client.close();
      }
   }


   @Test
   public void testTemplate() throws Exception
   {
      // fill out a path param and execute a get request
      Client client = ClientFactory.newClient();
      WebTarget target = client.target("http://localhost:9095/customers/{id}");
      Response response = target.resolveTemplate("id", "12345").request().get();
      try
      {
         Assert.assertEquals(200, response.getStatus());
         Customer cust = response.readEntity(Customer.class);
         Assert.assertEquals("Bill", cust.getName());
      }
      finally
      {
         response.close();
         client.close();
      }
   }

   @Test
   public void testAsync() throws Exception
   {
      // fill out a query param and execute a get request
      Client client = ClientFactory.newClient();
      WebTarget target = client.target("http://localhost:9095/customers");
      try
      {
         // execute in background
         Future<Customer> future = target.queryParam("name", "Bill").request().async().get(Customer.class);
         // wait 10 seconds for a response
         Customer cust = future.get(10, TimeUnit.SECONDS);
         Assert.assertEquals("Bill", cust.getName());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testAsyncCallback() throws Exception
   {
      // fill out a query param and execute a get request
      Client client = ClientFactory.newClient();
      WebTarget target = client.target("http://localhost:9095/customers");
      try
      {
         // execute in background
         final CountDownLatch latch = new CountDownLatch(1);
         target.queryParam("name", "Bill").request().async().get(new InvocationCallback<Customer>()
         {
            @Override
            public void completed(Customer customer)
            {
               System.out.println("Obtained customer: " + customer.getName());
               latch.countDown();
            }

            @Override
            public void failed(ClientException error)
            {
               latch.countDown();
            }
         });
         // await for callback to wake us up
         latch.await(10, TimeUnit.SECONDS);
      }
      finally
      {
         client.close();
      }
   }

   @Path("/customers")
   public interface CustomerProxy
   {
      @GET
      @Produces("application/json")
      Customer getCustomer(@QueryParam("name") String name);

   }

   @Test
   public void testProxy() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      // or you can do...
      // ResteasyClient client = (ResteasyClient)ClientFactory.newClient();
      CustomerProxy proxy = client.target("http://localhost:9095").proxy(CustomerProxy.class);
      Customer cust = proxy.getCustomer("Monica");
      Assert.assertEquals("Monica", cust.getName());
   }



}
