package org.hornetq.rest.test;

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
public class CreateDestinationTest extends MessageTestBase
{
   @BeforeClass
   public static void reg()
   {
      server.getJaxrsServer().getDeployment().getProviderFactory().registerProvider(org.jboss.resteasy.plugins.providers.DocumentProvider.class);
   }

   @Test
   public void testCreateQueue() throws Exception
   {
      String queueConfig = "<queue name=\"testQueue\"><durable>true</durable></queue>";
      ClientRequest create = new ClientRequest(generateURL("/queues"));
      ClientResponse cRes = create.body("application/hornetq.jms.queue+xml", queueConfig).post();
      Assert.assertEquals(201, cRes.getStatus());
      System.out.println("Location: " + cRes.getLocation());
      ClientRequest request = cRes.getLocation().request();

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-consumers");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("consumeNext: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      System.out.println(consumeNext);
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      session = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consumer");
      System.out.println("session: " + session);
      MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("consumeNext: " + consumeNext);

      Assert.assertEquals(204, session.request().delete().getStatus());
   }

   @Test
   public void testCreateTopic() throws Exception
   {
      String queueConfig = "<topic name=\"testTopic\"></topic>";
      ClientRequest create = new ClientRequest(generateURL("/topics"));
      ClientResponse cRes = create.body("application/hornetq.jms.topic+xml", queueConfig).post();
      Assert.assertEquals(201, cRes.getStatus());

      ClientRequest request = cRes.getLocation().request();

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), response, "create");
      Link subscriptions = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), response, "pull-subscriptions");


      ClientResponse res = subscriptions.request().post();
      Assert.assertEquals(201, res.getStatus());
      Link sub1 = res.getLocation();
      Assert.assertNotNull(sub1);
      Link consumeNext1 = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), res, "consume-next");
      Assert.assertNotNull(consumeNext1);
      System.out.println("consumeNext1: " + consumeNext1);


      res = subscriptions.request().post();
      Assert.assertEquals(201, res.getStatus());
      Link sub2 = res.getLocation();
      Assert.assertNotNull(sub2);
      Link consumeNext2 = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), res, "consume-next");
      Assert.assertNotNull(consumeNext1);


      res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext1.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext1 = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), res, "consume-next");

      res = consumeNext1.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      consumeNext1 = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), res, "consume-next");

      res = consumeNext2.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext2 = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), res, "consume-next");

      res = consumeNext2.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      consumeNext2 = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), res, "consume-next");
      Assert.assertEquals(204, sub1.request().delete().getStatus());
      Assert.assertEquals(204, sub2.request().delete().getStatus());
   }

}