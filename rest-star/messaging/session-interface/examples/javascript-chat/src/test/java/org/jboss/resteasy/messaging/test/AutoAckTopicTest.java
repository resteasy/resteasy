package org.jboss.resteasy.messaging.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.util.LinkStrategy;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AutoAckTopicTest
{
   public static Link getLinkByTitle(LinkStrategy strategy, ClientResponse response, String title)
   {
      return response.getLinkHeader().getLinkByTitle(title);
   }

   @Test
   public void testSuccessFirst() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/topics/jms.topic.chat");

      ClientResponse response = request.head();
      Assert.assertEquals("*****", 200, response.getStatus());
      Link sender = getLinkByTitle(null, response, "create");
      Link subscriptions = getLinkByTitle(null, response, "subscriptions");

       /*
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
      */
   }

}