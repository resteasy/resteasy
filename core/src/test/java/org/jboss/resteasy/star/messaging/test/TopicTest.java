package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.SimpleDeployment;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicTest extends BaseResourceTest
{
   public static SimpleDeployment server;

   @BeforeClass
   public static void setup() throws Exception
   {
      server = new SimpleDeployment();
      server.getTopics().add("test");
      server.setRegistry(deployment.getRegistry());
      server.start();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      server.stop();
   }


   @Test
   public void testPull() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/test"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("sender");
      Link top = response.getLinkHeader().getLinkByTitle("top");

      Assert.assertEquals(504, top.request().get().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(1)).post().getStatus());
      Link next = top;
      ClientResponse<String> res = next.request().get(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(1), res.getEntity());
      System.out.println("***: " + res.getLinkHeader());
      next = res.getLinkHeader().getLinkByTitle("next");
      Assert.assertEquals(504, next.request().get().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(2)).post().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(3)).post().getStatus());


      res = next.request().get(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(2), res.getEntity());

      System.out.println("***: " + res.getLinkHeader());
      next = res.getLinkHeader().getLinkByTitle("next");
      res = next.request().get(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(3), res.getEntity());


      System.out.println("***: " + res.getLinkHeader());
      next = res.getLinkHeader().getLinkByTitle("next");
      Assert.assertEquals(504, next.request().get().getStatus());
   }

   private static CountDownLatch listenerLatch;

   @Path("/listener")
   public static class Listener
   {
      @POST
      @Consumes("text/plain")
      public void post(String message)
      {
         System.out.println(message);
         listenerLatch.countDown();

      }
   }

   @Test
   public void testPush() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/test"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("sender");
      Link subscribers = response.getLinkHeader().getLinkByTitle("subscribers");


      listenerLatch = new CountDownLatch(1);
      response = subscribers.request().body("text/uri-list", "http://localhost:8085/listener").post();
      Assert.assertEquals(201, response.getStatus());
      String subscriber = (String) response.getHeaders().getFirst("Location");
      System.out.println("subscriber: " + subscriber);

      TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
      server.setPort(8085);
      server.start();
      server.getDeployment().getRegistry().addPerRequestResource(Listener.class);

      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(1)).post().getStatus());

      Assert.assertTrue(listenerLatch.await(2, TimeUnit.SECONDS));


   }
}
