package org.jboss.resteasy.test.profiling;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProfilingTest
{

   private static final Logger LOG = Logger.getLogger(ProfilingTest.class);

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
      InMemoryClientExecutor executor = new InMemoryClientExecutor();
      executor.getDispatcher().getRegistry().addPerRequestResource(JsonTest.class);

      final int ITERATIONS = 1000;

      long start = System.currentTimeMillis();
      for (int i = 0; i < ITERATIONS; i++)
      {
         ClientRequest request = new ClientRequest("/", executor);
         request.body("application/json", new Customer("bill", "burke"));
         String response = request.postTarget(String.class);
      }
      long end = System.currentTimeMillis() - start;
      LOG.info(ITERATIONS + " iterations took " + end + "ms");
   }
}
