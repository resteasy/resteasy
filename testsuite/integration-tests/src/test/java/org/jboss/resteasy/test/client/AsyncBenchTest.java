package org.jboss.resteasy.test.client;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.AsyncInvokeResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
@Ignore
public class AsyncBenchTest extends ClientTestBase
{

   static Client client;

   static Client nioClient;
   
   static final int ITERATIONS = 4000;
   static final int MAX_CONNECTIONS = 200;

   private static final Logger LOG = Logger.getLogger(AsyncBenchTest.class);

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(AsyncBenchTest.class.getSimpleName());
      war.addClass(AsyncBenchTest.class);
      war.addClass(ClientTestBase.class);
      return TestUtil.finishContainerPrepare(war, null, AsyncInvokeResource.class);
   }

   @After
   public void after() throws Exception
   {
      if (client != null)
         client.close();
      if (nioClient != null)
         nioClient.close();
   }

   @Test
   public void testAsyncPost() throws Exception
   {
      long start = System.currentTimeMillis();
      final String oldProp = System.getProperty("http.maxConnections");
      System.setProperty("http.maxConnections", String.valueOf(MAX_CONNECTIONS));
      nioClient = new ResteasyClientBuilder().useAsyncHttpEngine().build();
      WebTarget wt = nioClient.target(generateURL("/test"));
      runCallback(wt, "NIO");
      long end = System.currentTimeMillis() - start;
      LOG.info("TEST NON BLOCKING IO - " + ITERATIONS + " iterations took " + end + "ms");
      if (oldProp != null)
      {
         System.setProperty("http.maxConnections", oldProp);
      }
      else
      {
         System.clearProperty("http.maxConnections");
      }
   }
   
   @Test
   public void testPost() throws Exception
   {
      long start = System.currentTimeMillis();
      client = new ResteasyClientBuilder().connectionPoolSize(MAX_CONNECTIONS).maxPooledPerRoute(MAX_CONNECTIONS).build();
      WebTarget wt2 = client.target(generateURL("/test"));
      runCallback(wt2, "BIO");
      long end = System.currentTimeMillis() - start;
      LOG.info("TEST BLOCKING IO - " + ITERATIONS + " iterations took " + end + "ms");
   }
   
   private void runCallback(WebTarget wt, String msg) throws Exception
   {
      CountDownLatch latch = new CountDownLatch(ITERATIONS);
      for (int i = 0; i < ITERATIONS; i++) {
         final String m = msg + i;
         wt.request().async().post(Entity.text(m), new InvocationCallback<Response>()
         {
            @Override
            public void completed(Response response)
            {
               String entity = response.readEntity(String.class);
               Assert.assertEquals("post " + m, entity);
               latch.countDown();
            }
   
            @Override
            public void failed(Throwable error)
            {
               throw new RuntimeException(error);               
            }
         });
      }
      latch.await();
   }   
}
