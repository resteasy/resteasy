package org.jboss.resteasy.test.client.old;

import org.junit.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InMemoryTest
{
   @Path("/foo")
   public static class SimpleTest
   {

      @POST
      @Produces("text/plain")
      @Consumes("text/plain")
      public String create(String cust)
      {
         return cust;
      }

      @GET
      @Produces("text/plain")
      public String get(@HeaderParam("a") String a, @QueryParam("b") String b)
      {
         return a + " " + b;
      }

   }

   @Test
   public void testSimple() throws Exception
   {
      InMemoryClientExecutor executor = new InMemoryClientExecutor();
      executor.getDispatcher().getRegistry().addPerRequestResource(SimpleTest.class);
      ClientRequest request = new ClientRequest("/foo", executor);
      request.body("text/plain", "hello world");
      Assert.assertEquals("hello world", request.postTarget(String.class));

      request = new ClientRequest("/foo", executor);
      request.header("a", "hello");
      request.queryParameter("b", "world");
      Assert.assertEquals("hello world", request.getTarget(String.class));

   }
}