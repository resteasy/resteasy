package org.jboss.resteasy.test.profiling;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProfilingTest {
    public static class Customer {
        private String first;
        private String last;

        public Customer(final String first, final String last) {
            this.first = first;
            this.last = last;
        }

        public Customer() {
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }
    }

    @Path("/")
    public static class JsonTest {

        @POST
        @Produces("application/json")
        @Consumes("application/json")
        public Customer create(Customer cust) {
            return cust;
        }

    }

    @Test
    public void testJson() throws Exception {
        InMemoryClientEngine engine = new InMemoryClientEngine();
        engine.getDispatcher().getRegistry().addPerRequestResource(JsonTest.class);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        final int ITERATIONS = 1000;

        //      long start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            Builder request = client.target("/").request();
            request.post(Entity.entity(new Customer("bill", "burke"), "application/json"), String.class);
        }
        //      long end = System.currentTimeMillis() - start;
        //      System.out.println(ITERATIONS + " iterations took " + end + "ms");
    }
}
