package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.queue.QueueDeployment;
import org.junit.Assert;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RoundtripTimeTest extends BaseMessageTest
{
   @Test
   public void testSuccessFirst() throws Exception
   {
      QueueDeployment deployment = new QueueDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testQueue");
      manager.getQueueManager().deploy(deployment);

      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumeNext = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("consume-next: " + consumeNext);


      long start = System.currentTimeMillis();
      int num = 100;
      for (int i = 0; i < num; i++)
      {
         sender.request().body("text/plain", Integer.toString(i + 1)).post();
      }
      long end = System.currentTimeMillis() - start;
      System.out.println(num + " iterations took " + end + "ms");

      for (int i = 0; i < num; i++)
      {
         ClientResponse res = consumeNext.request().post(String.class);
         consumeNext = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
         Assert.assertEquals(200, res.getStatus());
         Assert.assertEquals(Integer.toString(i + 1), res.getEntity(String.class));
      }
   }

}