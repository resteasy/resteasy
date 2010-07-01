package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.queue.QueueDeployment;
import org.jboss.resteasy.star.messaging.queue.QueueServiceManager;
import org.jboss.resteasy.star.messaging.util.Constants;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AutoAckQueueTest extends BaseMessageTest
{
   public static QueueServiceManager manager;

   @BeforeClass
   public static void setup() throws Exception
   {
      manager = new QueueServiceManager();
      manager.setRegistry(deployment.getRegistry());
      manager.start();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      manager.stop();
   }

   @Test
   public void testSuccessFirst() throws Exception
   {
      QueueDeployment deployment = new QueueDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testQueue");
      manager.deploy(deployment);

      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + sender);
      Link consumeNext = response.getLinkHeader().getLinkByTitle("consume-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = res.getLinkHeader().getLinkByTitle("session");
      System.out.println("session: " + session);
      consumeNext = res.getLinkHeader().getLinkByTitle("consume-next");
      System.out.println("consumeNext: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      System.out.println(consumeNext);
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      session = res.getLinkHeader().getLinkByTitle("session");
      System.out.println("session: " + session);
      res.getLinkHeader().getLinkByTitle("consume-next");
      System.out.println("consumeNext: " + consumeNext);

      Assert.assertEquals(204, session.request().delete().getStatus());
   }

}