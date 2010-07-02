package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.queue.QueueDeployment;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DupQueueTest extends BaseMessageTest
{
   @Test
   public void testDup() throws Exception
   {
      QueueDeployment deployment = new QueueDeployment();
      deployment.setDuplicatesAllowed(false);
      deployment.setDurableSend(false);
      deployment.setName("testQueue");
      manager.getQueueManager().deploy(deployment);

      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumeNext = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(307, res.getStatus());
      sender = res.getLocation();
      System.out.println("create-next: " + sender);
      Assert.assertNotNull(sender);
      res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      sender = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "create-next");
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "session");
      System.out.println("session: " + session);
      consumeNext = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("consumeNext: " + consumeNext);

      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      session = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "session");
      System.out.println("session: " + session);
      consumeNext = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
      System.out.println("consumeNext: " + consumeNext);
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(503, res.getStatus());

      Assert.assertEquals(204, session.request().delete().getStatus());
   }

}