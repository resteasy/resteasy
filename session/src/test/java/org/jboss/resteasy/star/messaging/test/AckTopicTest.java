package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.topic.TopicDeployment;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AckTopicTest extends BaseMessageTest
{

   @BeforeClass
   public static void setup() throws Exception
   {
      TopicDeployment deployment1 = new TopicDeployment("testQueue", true);
      manager.getTopicManager().deploy(deployment1);
   }

   @Test
   public void testAckTimeout() throws Exception
   {
      TopicDeployment deployment = new TopicDeployment();
      deployment.setConsumerSessionTimeoutSeconds(1);
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testAck");
      manager.getTopicManager().deploy(deployment);


      ClientRequest request = new ClientRequest(generateURL("/topics/testAck"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + sender);
      Link subscriptions = response.getLinkHeader().getLinkByTitle("subscriptions");
      response = subscriptions.request().formParameter("autoAck", "false")
              .formParameter("durable", "true")
              .post();
      Assert.assertEquals(201, response.getStatus());
      Link sub1 = response.getLocation();
      Assert.assertNotNull(sub1);


      System.out.println(response.getLinkHeader());
      Link consumeNext = response.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println("poller: " + consumeNext);

      {
         ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
         Assert.assertEquals(201, res.getStatus());


         res = consumeNext.request().post(String.class);
         Assert.assertEquals(200, res.getStatus());
         Link ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
         System.out.println("ack: " + ack);
         Assert.assertNotNull(ack);
         Link session = res.getLinkHeader().getLinkByTitle("session");
         System.out.println("session: " + session);
         consumeNext = res.getLinkHeader().getLinkByTitle("acknowledge-next");
         System.out.println("consumeNext: " + consumeNext);

         Thread.sleep(2000);

         ClientResponse ackRes = ack.request().formParameter("acknowledge", "true").post();
         if (ackRes.getStatus() == 500)
         {
            System.out.println("Failure: " + ackRes.getEntity(String.class));
         }
         Assert.assertEquals(412, ackRes.getStatus());
         System.out.println("**** Successfully failed ack");
         System.out.println(ackRes.getLinkHeader());
         consumeNext = ackRes.getLinkHeader().getLinkByTitle("acknowledge-next");
         System.out.println("consumeNext: " + consumeNext);
      }
      {
         ClientResponse res = consumeNext.request().header(Constants.WAIT_HEADER, "2").post(String.class);
         Assert.assertEquals(200, res.getStatus());
         Link ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
         System.out.println("ack: " + ack);
         Assert.assertNotNull(ack);
         consumeNext = res.getLinkHeader().getLinkByTitle("acknowledge-next");
         System.out.println("consumeNext: " + consumeNext);

         ClientResponse ackRes = ack.request().formParameter("acknowledge", "true").post();
         if (ackRes.getStatus() != 204)
         {
            System.out.println(ackRes.getEntity(String.class));
         }
         Assert.assertEquals(204, ackRes.getStatus());
      }
      Assert.assertEquals(204, sub1.request().delete().getStatus());


   }

   @Test
   public void testSuccessFirst() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + sender);


      Link subscriptions = response.getLinkHeader().getLinkByTitle("subscriptions");
      response = subscriptions.request().formParameter("autoAck", "false")
              .formParameter("durable", "true")
              .post();
      Assert.assertEquals(201, response.getStatus());
      Link sub1 = response.getLocation();
      Assert.assertNotNull(sub1);
      System.out.println(response.getLinkHeader());
      Link consumeNext = response.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      System.out.println("call ack next");
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Link ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
      System.out.println("ack: " + ack);
      Assert.assertNotNull(ack);
      Link session = res.getLinkHeader().getLinkByTitle("session");
      System.out.println("session: " + session);
      consumeNext = res.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println("consumeNext: " + consumeNext);
      ClientResponse ackRes = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, ackRes.getStatus());
      consumeNext = ackRes.getLinkHeader().getLinkByTitle("acknowledge-next");

      System.out.println("sending next...");
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      System.out.println(consumeNext);
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
      System.out.println("ack: " + ack);
      Assert.assertNotNull(ack);
      res.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println("consumeNext: " + consumeNext);
      ackRes = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, ackRes.getStatus());

      Assert.assertEquals(204, sub1.request().delete().getStatus());
   }

   @Test
   public void testPull() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + sender);
      Link subscriptions = response.getLinkHeader().getLinkByTitle("subscriptions");
      response = subscriptions.request().formParameter("autoAck", "false")
              .formParameter("durable", "true")
              .post();
      Assert.assertEquals(201, response.getStatus());
      Link sub1 = response.getLocation();
      Assert.assertNotNull(sub1);
      System.out.println(response.getLinkHeader());
      Link consumeNext = response.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse<String> res = consumeNext.request().post(String.class);
      Assert.assertEquals(503, res.getStatus());
      consumeNext = res.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println(consumeNext);
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(1)).post().getStatus());
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(1), res.getEntity());
      Link ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
      System.out.println("ack: " + ack);
      ClientResponse ackRes = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, ackRes.getStatus());
      Assert.assertEquals(503, consumeNext.request().post().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(2)).post().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(3)).post().getStatus());


      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(2), res.getEntity());
      ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
      System.out.println("ack: " + ack);
      ackRes = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, ackRes.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(3), res.getEntity());
      ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
      System.out.println("ack: " + ack);
      ackRes = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, ackRes.getStatus());

      Assert.assertEquals(503, consumeNext.request().post().getStatus());
      Assert.assertEquals(204, sub1.request().delete().getStatus());


   }


   @Test
   public void testAcknowledgeNext() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + sender);
      Link subscriptions = response.getLinkHeader().getLinkByTitle("subscriptions");
      response = subscriptions.request().formParameter("autoAck", "false")
              .formParameter("durable", "true")
              .post();
      Assert.assertEquals(201, response.getStatus());
      Link sub1 = response.getLocation();
      Assert.assertNotNull(sub1);
      System.out.println(response.getLinkHeader());
      Link consumeNext = response.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      consumeNext = res.getLinkHeader().getLinkByTitle("acknowledge-next");
      System.out.println(consumeNext);
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      Link ack = res.getLinkHeader().getLinkByTitle("acknowledgement");
      System.out.println("ack: " + ack);
      Assert.assertNotNull(ack);
      ClientResponse ackRes = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, ackRes.getStatus());

      Assert.assertEquals(204, sub1.request().delete().getStatus());
   }
}