package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import io.netty.handler.codec.http.HttpHeaderValues;

import javax.ws.rs.client.Invocation.Builder;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncTest
{
   static String BASE_URI = generateURL("");
   static Client client;

   static final int REQUEST_TIMEOUT = 1000;

   @BeforeClass
   public static void setupSuite() throws Exception
   {
      NettyContainer.start().getRegistry().addSingletonResource(new AsyncJaxrsResource());
   }

   @AfterClass
   public static void tearDownSuite() throws Exception
   {
      NettyContainer.stop();
   }

   @Before
   public void setupTest() throws Exception
   {
      client = ClientBuilder.newClient();
   }

   @After
   public void tearDownTest() throws Exception
   {
      client.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testInjectionFailure()
   {
//      System.out.println("***INJECTION FAILURE***");
      Response response = client.target(BASE_URI).path("jaxrs/injection-failure/abcd").request().get();
      Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      response.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testMethodFailure() throws Exception
   {
//      System.out.println("***method FAILURE***");
      Response response = client.target(BASE_URI).path("jaxrs/method-failure").request().get();
      Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
      response.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testAsync() throws Exception
   {
      callAsync(client);
      //callAsync(client);
      //callAsync(client);
   }

   private void callAsync(Client client)
   {
      Response response = client.target(BASE_URI).path("jaxrs").request().get();
      Assert.assertEquals(200, response.getStatus());
//      System.out.println(response.getHeaders().size());
//      System.out.println(response.getHeaders().keySet().iterator().next());
      Assert.assertEquals("hello", response.readEntity(String.class));
      response.close();
   }

   @Test(timeout=3*REQUEST_TIMEOUT)
   public void testEmpty() throws Exception
   {
      callEmpty(client);
   }

   private void callEmpty(Client client)
   {
      long start = System.currentTimeMillis();
      Response response = client.target(BASE_URI).path("jaxrs/empty").request().get();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(204, response.getStatus());
      Assert.assertTrue(end < REQUEST_TIMEOUT);  // should take less than 1 second
      response.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testTimeout() throws Exception
   {
      Response response = client.target(BASE_URI).path("jaxrs/timeout").request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testCancelled() throws Exception
   {
      Response response = null;
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().put(null);
      Assert.assertEquals(204, response.getStatus());
      response.close();

      Thread.sleep(100);

      response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
      Assert.assertEquals(500, response.getStatus());
      response.close();

      Thread.sleep(100);

   }

   @Test
   public void testCancel() throws Exception
   {
      Response response = null;
      response = client.target(BASE_URI).path("jaxrs/cancelled").request().put(null);
      Assert.assertEquals(204, response.getStatus());
      response.close();

      Thread.sleep(100);

      response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
      Assert.assertEquals(500, response.getStatus());
      response.close();

      Thread.sleep(100);

      response = client.target(BASE_URI).path("jaxrs/cancel").request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();

      Thread.sleep(100);

      response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();

      Thread.sleep(100);
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testResumeObject() throws Exception
   {
      Response response = client.target(BASE_URI).path("jaxrs/resume/object").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bill", response.readEntity(XmlData.class).getName());
      response.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testResumeObjectThread() throws Exception
   {
      Response response = client.target(BASE_URI).path("jaxrs/resume/object/thread").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bill", response.readEntity(XmlData.class).getName());
      response.close();
   }

   @Test(timeout=REQUEST_TIMEOUT)
   public void testConnectionCloseHeader() throws Exception
   {
      Builder requestBuilder = client.target(BASE_URI).path("jaxrs/empty").request();
      requestBuilder.header("Connection", "close");
      Response response = requestBuilder.get();
      Assert.assertEquals(HttpHeaderValues.CLOSE.toString(), response.getHeaderString("Connection"));
      response.close();
   }
}
