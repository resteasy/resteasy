package org.jboss.resteasy.test.profiling;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProfilingTest
{
   public static class Customer
   {
      private String first;
      private String last;

      public Customer(String first, String last)
      {
         this.first = first;
         this.last = last;
      }

      public Customer()
      {
      }

      public String getFirst()
      {
         return first;
      }

      public void setFirst(String first)
      {
         this.first = first;
      }

      public String getLast()
      {
         return last;
      }

      public void setLast(String last)
      {
         this.last = last;
      }
   }


   @Path("/")
   public static class JsonTest
   {

      @POST
      @Produces("application/json")
      @Consumes("application/json")
      public Customer create(Customer cust)
      {
         return cust;
      }

   }

   @Test
   public void testJson() throws Exception
   {
      InMemoryClientEngine engine = new InMemoryClientEngine();
      engine.getDispatcher().getRegistry().addPerRequestResource(JsonTest.class);
      Client client = new ResteasyClientBuilder().httpEngine(engine).build();
      final int ITERATIONS = 1000;

//      long start = System.currentTimeMillis();
      for (int i = 0; i < ITERATIONS; i++)
      {
         Builder request = client.target("/").request();
         request.post(Entity.entity(new Customer("bill", "burke"), "application/json"), String.class);
      }
//      long end = System.currentTimeMillis() - start;
//      System.out.println(ITERATIONS + " iterations took " + end + "ms");
   }
}
