package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RESTEASY-741.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 21, 2012
 */
public class InputStreamCloseTestCase
{
   protected ResteasyDeployment deployment;
   
   static class TestInputStream extends ByteArrayInputStream
   {
      private boolean closed;
      
      public TestInputStream(byte[] b)
      {
         super(b);
      }
      
      public void close() throws IOException
      {
         super.close();
         closed = true;
      }
      
      public boolean isClosed()
      {
         return closed;
      }
   }
   
   @Path("/")
   static public class TestResource
   {
      static private TestInputStream inputStream;
      
      @GET
      @Produces("text/plain")
      @Path("create")
      public InputStream create()
      {
         System.out.println("entered create()");
         inputStream = new TestInputStream("hello".getBytes());
         return inputStream;
      }
      
      @GET
      @Path("test")
      public Response test()
      {
         System.out.println("entered test()");
         return (inputStream.isClosed() ? Response.ok().build() : Response.serverError().build());
      }
   }
   
   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void test() throws Exception
   {
      // Resource creates and returns InputStream.
      ClientRequest request = new ClientRequest("http://localhost:8081/create/");
      System.out.println("Sending create request");
      ClientResponse<?> response = request.get(String.class);
      System.out.println("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.getEntity());
      
      // Verify previously created InputStream has been closed.
      request = new ClientRequest("http://localhost:8081/test/");
      System.out.println("Sending test request");
      response = request.get();
      System.out.println("Test status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
   }
}
