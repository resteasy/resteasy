package org.jboss.resteasy.star.messaging.test;

import org.hornetq.api.core.SimpleString;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.junit.Assert;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FindDestinationTest extends BaseMessageTest
{
   @Test
   public void testFindQueue() throws Exception
   {
      server.createQueue(new SimpleString("testQueue"), new SimpleString("testQueue"), null, false, false);

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
      Assert.assertEquals(204, session.request().delete().getStatus());
   }

   @Test
   public void testFindTopic() throws Exception
   {
      server.createQueue(new SimpleString("testTopic"), new SimpleString("testTopic"), null, false, false);
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
