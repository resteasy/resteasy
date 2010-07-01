package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.topic.TopicDeployment;
import org.jboss.resteasy.star.messaging.topic.TopicServiceManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AutoAckTopicTest extends BaseMessageTest
{
   public static TopicServiceManager topicDeployer;

   @BeforeClass
   public static void setup() throws Exception
   {
      topicDeployer = new TopicServiceManager();
      topicDeployer.setRegistry(deployment.getRegistry());
      topicDeployer.start();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      topicDeployer.stop();
   }

   @Test
   public void testSuccessFirst() throws Exception
   {
      TopicDeployment deployment = new TopicDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testTopic");
      topicDeployer.deploy(deployment);

      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("create");
      Link subscriptions = response.getLinkHeader().getLinkByTitle("subscriptions");


      ClientResponse res = subscriptions.request().post();
      Assert.assertEquals(201, res.getStatus());
      Link sub1 = res.getLocation();
      Assert.assertNotNull(sub1);
      Link consumeNext1 = res.getLinkHeader().getLinkByTitle("consume-next");
      Assert.assertNotNull(consumeNext1);
      System.out.println("consumeNext1: " + consumeNext1);


      res = subscriptions.request().post();
      Assert.assertEquals(201, res.getStatus());
      Link sub2 = res.getLocation();
      Assert.assertNotNull(sub2);
      Link consumeNext2 = res.getLinkHeader().getLinkByTitle("consume-next");
      Assert.assertNotNull(consumeNext1);


      res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext1.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext1 = res.getLinkHeader().getLinkByTitle("consume-next");

      res = consumeNext1.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      consumeNext1 = res.getLinkHeader().getLinkByTitle("consume-next");

      res = consumeNext2.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext2 = res.getLinkHeader().getLinkByTitle("consume-next");

      res = consumeNext2.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      consumeNext2 = res.getLinkHeader().getLinkByTitle("consume-next");
      Assert.assertEquals(204, sub1.request().delete().getStatus());
      Assert.assertEquals(204, sub2.request().delete().getStatus());
   }

}