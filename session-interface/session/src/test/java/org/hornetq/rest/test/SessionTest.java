package org.hornetq.rest.test;

import org.hornetq.rest.queue.QueueDeployment;
import org.hornetq.rest.topic.TopicDeployment;
import org.hornetq.rest.util.Constants;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SessionTest extends MessageTestBase
{
   @BeforeClass
   public static void setup() throws Exception
   {
      QueueDeployment deployment1 = new QueueDeployment("testQueue", true);
      manager.getQueueManager().deploy(deployment1);
      TopicDeployment deployment = new TopicDeployment();
      deployment.setConsumerSessionTimeoutSeconds(1);
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testTopic");
      manager.getTopicManager().deploy(deployment);
   }

   @Test
   public void testRestartFromAutoAckSession() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-consumers");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link session = response.getLocation();
      response = session.request().head();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("consume-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      response = session.request().get();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");

      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      response = session.request().head();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");

      res = sender.request().body("text/plain", Integer.toString(3)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("3", res.getEntity(String.class));

      Assert.assertEquals(204, session.request().delete().getStatus());
   }


   @Test
   public void testTopicRestartFromAutoAckSession() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link session = response.getLocation();
      response = session.request().head();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("consume-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      response = session.request().get();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");

      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      response = session.request().head();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");

      res = sender.request().body("text/plain", Integer.toString(3)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("3", res.getEntity(String.class));

      Assert.assertEquals(204, session.request().delete().getStatus());
   }


   @Test
   public void testRestartFromAckSession() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-consumers");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link session = response.getLocation();
      response = session.request().head();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("consume-next: " + consumeNext);
      Link ack = null;

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      // consume
      res = consumeNext.request().header(Constants.WAIT_HEADER, "1").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      response = session.request().get();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNotNull(ack);

      // acknowledge
      res = ack.request().formParameter("acknowledge", "true").post();
      response = session.request().head();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNotNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNull(ack);

      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      // consume
      res = consumeNext.request().header(Constants.WAIT_HEADER, "1").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      response = session.request().get();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNotNull(ack);

      // acknowledge
      res = ack.request().formParameter("acknowledge", "true").post();
      response = session.request().head();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNotNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNull(ack);

      Assert.assertEquals(204, session.request().delete().getStatus());
   }

   @Test
   public void testTopicRestartFromAckSession() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link session = response.getLocation();
      response = session.request().head();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("consume-next: " + consumeNext);
      Link ack = null;

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      // consume
      res = consumeNext.request().header(Constants.WAIT_HEADER, "1").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      response = session.request().get();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNotNull(ack);

      // acknowledge
      res = ack.request().formParameter("acknowledge", "true").post();
      response = session.request().head();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNotNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNull(ack);

      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      // consume
      res = consumeNext.request().header(Constants.WAIT_HEADER, "1").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      response = session.request().get();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNotNull(ack);

      // acknowledge
      res = ack.request().formParameter("acknowledge", "true").post();
      response = session.request().head();
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      Assert.assertNotNull(consumeNext);
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledgement");
      Assert.assertNull(ack);

      Assert.assertEquals(204, session.request().delete().getStatus());
   }
}
