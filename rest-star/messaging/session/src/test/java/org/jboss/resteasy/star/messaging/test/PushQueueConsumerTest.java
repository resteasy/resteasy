package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.queue.QueueDeployment;
import org.jboss.resteasy.star.messaging.queue.QueueServiceManager;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;
import org.jboss.resteasy.star.messaging.queue.push.xml.XmlLink;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushQueueConsumerTest extends BaseMessageTest
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
      QueueDeployment deployment2 = new QueueDeployment();
      deployment2.setDuplicatesAllowed(true);
      deployment2.setDurableSend(false);
      deployment2.setName("forwardQueue");
      manager.deploy(deployment2);

      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + sender);
      Link pushSubscriptions = response.getLinkHeader().getLinkByTitle("push-subscriptions");
      System.out.println("push subscriptions: " + pushSubscriptions);

      request = new ClientRequest(generateURL("/queues/forwardQueue"));
      response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link forwardSender = response.getLinkHeader().getLinkByTitle("create");
      System.out.println("create: " + forwardSender);
      Link consumeNext = response.getLinkHeader().getLinkByTitle("consume-next");
      System.out.println("poller: " + consumeNext);

      PushRegistration reg = new PushRegistration();
      reg.setDurable(false);
      XmlLink target = new XmlLink(forwardSender);
      reg.setTarget(target);
      response = pushSubscriptions.request().body("application/xml", reg).post();
      Assert.assertEquals(201, response.getStatus());

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      Thread.sleep(100);
      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = res.getLinkHeader().getLinkByTitle("session");
      Assert.assertEquals(204, session.request().delete().getStatus());
   }
}
