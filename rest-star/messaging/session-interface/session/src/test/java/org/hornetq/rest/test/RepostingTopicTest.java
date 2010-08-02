package org.hornetq.rest.test;

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
 * repost on same consume-next
 * repost on old consume-next
 * repost on same consume-next with timeouts
 * repost on same ack-next
 * repost successful ack
 * repost successful unack
 * repost ack after unack
 * repost unack after ack
 * post on old ack-next
 * post on old ack-next after an ack
 * ack with an old ack link
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RepostingTopicTest extends MessageTestBase
{
   @BeforeClass
   public static void setup() throws Exception
   {
      TopicDeployment deployment1 = new TopicDeployment("testTopic", true);
      manager.getTopicManager().deploy(deployment1);
   }

   @Test
   public void testReconnectOnNamedSubscriber() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("name", "bill").post();

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      Thread.sleep(1000); // sleep to make sure messages get enqueued.

      // recreate subscription a second time as named.  Should pick up old one.

      response = consumers.request().formParameter("name", "bill").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("resource consume-next: " + consumeNext);
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));


      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testRestartOnDurableNamedSubscriber() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("name", "bill").formParameter("durable", "true").post();

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      Thread.sleep(1000); // sleep to make sure messages get enqueued.

      manager.getTopicManager().getDestination().findTopic("testTopic").getSubscriptions().stop();

      // recreate subscription a second time as named.  Should pick up old one.

      response = consumers.request().formParameter("name", "bill").formParameter("durable", "true").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("resource consume-next: " + consumeNext);
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));


      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testRestartOnNonDurableNamedSubscriber() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("name", "bill").post();

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      Thread.sleep(1000); // sleep to make sure messages get enqueued.

      manager.getTopicManager().getDestination().findTopic("testTopic").getSubscriptions().stop();

      // recreate subscription a second time as named.  Should pick up old one.

      response = consumers.request().formParameter("name", "bill").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("resource consume-next: " + consumeNext);
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(503, res.getStatus());

      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testPostOnSameConsumeNext() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("resource consume-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("session 1st consumeNext: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));


      session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("session 2nd consumeNext: " + consumeNext);

      res = sender.request().body("text/plain", Integer.toString(3)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("3", res.getEntity(String.class));

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testPostOnOldConsumeNext() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("resource consume-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      Link firstConsumeNext = consumeNext;
      System.out.println("session 1st consumeNext: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("session 2nd consumeNext: " + consumeNext);

      res = sender.request().body("text/plain", Integer.toString(3)).post();
      Assert.assertEquals(201, res.getStatus());
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("3", res.getEntity(String.class));

      res = firstConsumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(412, res.getStatus());
      System.out.println(res.getEntity(String.class));

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testPostOnSameConsumeNextWithTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("resource consume-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("session 1st consumeNext: " + consumeNext);

      // test timeout here
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(503, res.getStatus());
      session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("session 2nd consumeNext: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(3)).post();
      Assert.assertEquals(201, res.getStatus());
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("3", res.getEntity(String.class));

      Assert.assertEquals(204, session.request().delete().getStatus());
   }

   @Test
   public void testPostOnSameAcknowledgeNextAndAck() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("resource acknowledge-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      Link ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");
      System.out.println("session 1st acknowledge-next: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testRepostSuccessfulUnacknowledge() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("resource acknowledge-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      Link ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "false").post();
      Assert.assertEquals(204, res.getStatus());
      res = ack.request().formParameter("acknowledge", "false").post();
      Assert.assertEquals(204, res.getStatus());
      res = ack.request().formParameter("acknowledge", "false").post();
      Assert.assertEquals(204, res.getStatus());
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");
      System.out.println("session 1st acknowledge-next: " + consumeNext);


      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testRepostAckAfterUnacknowledge() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("resource acknowledge-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      Link ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "false").post();
      Assert.assertEquals(204, res.getStatus());
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(412, res.getStatus());
      System.out.println(res.getEntity(String.class));
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");
      System.out.println("session 1st acknowledge-next: " + consumeNext);


      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testRepostUnAckAfterAcknowledge() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("resource acknowledge-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      Link ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());
      res = ack.request().formParameter("acknowledge", "false").post();
      Assert.assertEquals(412, res.getStatus());
      System.out.println(res.getEntity(String.class));
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");
      System.out.println("session 1st acknowledge-next: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

   @Test
   public void testPostOnOldAcknowledgeNextAndAck() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-subscriptions");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "false").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "acknowledge-next");
      System.out.println("resource acknowledge-next: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "1").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      Link ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      Link oldAck = ack;
      System.out.println("ack: " + ack);
      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");
      System.out.println("session 1st acknowledge-next: " + consumeNext);
      Link firstConsumeNext = consumeNext;

      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      ack = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledgement");
      System.out.println("ack: " + ack);

      res = oldAck.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(412, res.getStatus());
      System.out.println(res.getEntity(String.class));
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");

      res = ack.request().formParameter("acknowledge", "true").post();
      Assert.assertEquals(204, res.getStatus());
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "acknowledge-next");


      res = consumeNext.request().post(String.class);
      Assert.assertEquals(503, res.getStatus());

      Assert.assertEquals(204, session.request().delete().getStatus());


   }

}
