package org.jboss.resteasy.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncTest
{
   static String BASE_URI = generateURL("");
   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      NettyContainer.start().getRegistry().addSingletonResource(new AsyncJaxrsResource());
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      NettyContainer.stop();
   }

   @Test
   public void testInjectionFailure() throws Exception
   {
      System.out.println("***INJECTION FAILURE***");
      long start = System.currentTimeMillis();
      Client client = ClientBuilder.newClient();
      Response response = client.target(BASE_URI).path("jaxrs/injection-failure/abcd").request().get();
      Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      long end = System.currentTimeMillis() - start;
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }

   @Test
   public void testMethodFailure() throws Exception
   {
      System.out.println("***method FAILURE***");
      long start = System.currentTimeMillis();
      Client client = ClientBuilder.newClient();
      Response response = client.target(BASE_URI).path("jaxrs/method-failure").request().get();
      Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
      long end = System.currentTimeMillis() - start;
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }



   @Test
   public void testAsync() throws Exception
   {
      Client client = ClientBuilder.newClient();
      callAsync(client);
      //callAsync(client);
      //callAsync(client);
      client.close();
   }

   private void callAsync(Client client)
   {
      long start = System.currentTimeMillis();
      Response response = client.target(BASE_URI).path("jaxrs").request().get();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.getHeaders().size());
      System.out.println(response.getHeaders().keySet().iterator().next());
      Assert.assertEquals("hello", response.readEntity(String.class));
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
   }

   @Test
   public void testEmpty() throws Exception
   {
      Client client = ClientBuilder.newClient();
      callEmpty(client);
      callEmpty(client);
      callEmpty(client);
      client.close();
   }

   private void callEmpty(Client client)
   {
      long start = System.currentTimeMillis();
      Response response = client.target(BASE_URI).path("jaxrs/empty").request().get();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(204, response.getStatus());
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
   }


   @Test
   public void testTimeout() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(BASE_URI).path("jaxrs/timeout").request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
      client.close();
   }

   @Test
   public void testCancelled() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = null;
      System.out.println("calling cancelled");
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().put(null);
      Assert.assertEquals(204, response.getStatus());
      response.close();
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
      System.out.println("returned from calling cancelled");
      Assert.assertEquals(500, response.getStatus());
      System.out.println("done");

      response.close();
      client.close();
   }


   @Test
   public void testCancel() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = null;
      System.out.println("calling cancelled");
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().put(null);
      Assert.assertEquals(204, response.getStatus());
      response.close();
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
      System.out.println("returned from calling cancelled");
      Assert.assertEquals(500, response.getStatus());
      System.out.println("done");
      response.close();

      System.out.println("calling cancel");
      response = client.target(BASE_URI).path("jaxrs/cancel").request().get();
      System.out.println("got response");
      Assert.assertEquals(503, response.getStatus());
      response.close();
      System.out.println("calling cancelled");
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
      System.out.println("returned from calling cancelled");
      Assert.assertEquals(204, response.getStatus());
      System.out.println("done");

      response.close();
      client.close();
   }

   @Test
   public void testResumeObject() throws Exception
   {
      Client client = ClientBuilder.newClient();
      long start = System.currentTimeMillis();
      Response response = client.target(BASE_URI).path("jaxrs/resume/object").request().get();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bill", response.readEntity(XmlData.class).getName());
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }

   @Test
   public void testResumeObjectThread() throws Exception
   {
      Client client = ClientBuilder.newClient();
      long start = System.currentTimeMillis();
      Response response = client.target(BASE_URI).path("jaxrs/resume/object/thread").request().get();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bill", response.readEntity(XmlData.class).getName());
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }


}
