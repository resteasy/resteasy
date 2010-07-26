package org.hornetq.rest.test;

import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQDestination;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.hornetq.rest.HttpHeaderProperty;
import org.hornetq.rest.Jms;
import org.hornetq.rest.queue.QueueDeployment;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JMSTest extends MessageTestBase
{
   public static ConnectionFactory connectionFactory;

   @BeforeClass
   public static void setup() throws Exception
   {
      connectionFactory = new HornetQConnectionFactory(manager.getQueueManager().getSessionFactory());
   }

   @XmlRootElement
   public static class Order implements Serializable
   {
      private String name;
      private String amount;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public String getAmount()
      {
         return amount;
      }

      public void setAmount(String amount)
      {
         this.amount = amount;
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         Order order = (Order) o;

         if (!amount.equals(order.amount)) return false;
         if (!name.equals(order.name)) return false;

         return true;
      }

      @Override
      public int hashCode()
      {
         int result = name.hashCode();
         result = 31 * result + amount.hashCode();
         return result;
      }
   }

   public static Destination createDestination(String dest)
   {
      HornetQDestination destination = (HornetQDestination) HornetQDestination.fromAddress(dest);
      System.out.println("SimpleAddress: " + destination.getSimpleAddress());
      return destination;
   }

   public static void publish(String dest, Serializable object, String contentType) throws Exception
   {
      Connection conn = connectionFactory.createConnection();
      try
      {
         Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
         Destination destination = createDestination(dest);
         MessageProducer producer = session.createProducer(destination);
         ObjectMessage message = session.createObjectMessage();

         if (contentType != null)
         {
            message.setStringProperty(HttpHeaderProperty.CONTENT_TYPE, contentType);
         }
         message.setObject(object);

         producer.send(message);
      }
      finally
      {
         conn.close();
      }
      Thread.sleep(10);
   }


   public static class Listener implements MessageListener
   {
      public static Order order;
      public static CountDownLatch latch = new CountDownLatch(1);

      @Override
      public void onMessage(Message message)
      {
         try
         {
            order = Jms.getEntity(message, Order.class);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         latch.countDown();
      }
   }

   @Test
   public void testJmsConsumer() throws Exception
   {
      String queueName = HornetQDestination.createQueueAddressFromName("testQueue2").toString();
      System.out.println("Queue name: " + queueName);
      QueueDeployment deployment = new QueueDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName(queueName);
      manager.getQueueManager().deploy(deployment);
      Connection conn = connectionFactory.createConnection();
      try
      {
         Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
         Destination destination = createDestination(queueName);
         MessageConsumer consumer = session.createConsumer(destination);
         consumer.setMessageListener(new Listener());
         conn.start();

         ClientRequest request = new ClientRequest(generateURL("/queues/" + queueName));

         ClientResponse response = request.head();
         Assert.assertEquals(200, response.getStatus());
         Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
         System.out.println("create: " + sender);
         Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
         System.out.println("consume-next: " + consumeNext);

         // test that Accept header is used to set content-type
         {
            Order order = new Order();
            order.setName("1");
            order.setAmount("$5.00");
            response = sender.request().body("application/xml", order).post();
            Assert.assertEquals(201, response.getStatus());

            Listener.latch.await(1, TimeUnit.SECONDS);
            Assert.assertNotNull(Listener.order);
            Assert.assertEquals(order, Listener.order);
         }

      }
      finally
      {
         conn.close();
      }
   }


   @Test
   public void testJmsProducer() throws Exception
   {
      String queueName = HornetQDestination.createQueueAddressFromName("testQueue").toString();
      System.out.println("Queue name: " + queueName);
      QueueDeployment deployment = new QueueDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName(queueName);
      manager.getQueueManager().deploy(deployment);
      ClientRequest request = new ClientRequest(generateURL("/queues/" + queueName));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link consumers = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "pull-consumers");
      System.out.println("pull: " + consumers);
      response = consumers.request().formParameter("autoAck", "true").post();
      Link consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("consume-next: " + consumeNext);

      // test that Accept header is used to set content-type
      {
         Order order = new Order();
         order.setName("1");
         order.setAmount("$5.00");
         publish(queueName, order, null);

         ClientResponse res = consumeNext.request().accept("application/xml").post(String.class);
         Assert.assertEquals(200, res.getStatus());
         Assert.assertEquals("application/xml", res.getHeaders().getFirst("Content-Type").toString().toLowerCase());
         Order order2 = (Order) res.getEntity(Order.class);
         Assert.assertEquals(order, order2);
         consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
         Assert.assertNotNull(consumeNext);
      }

      // test that Accept header is used to set content-type
      {
         Order order = new Order();
         order.setName("1");
         order.setAmount("$5.00");
         publish(queueName, order, null);

         ClientResponse res = consumeNext.request().accept("application/json").post(String.class);
         Assert.assertEquals(200, res.getStatus());
         Assert.assertEquals("application/json", res.getHeaders().getFirst("Content-Type").toString().toLowerCase());
         Order order2 = (Order) res.getEntity(Order.class);
         Assert.assertEquals(order, order2);
         consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
         Assert.assertNotNull(consumeNext);
      }

      // test that message property is used to set content type
      {
         Order order = new Order();
         order.setName("2");
         order.setAmount("$15.00");
         publish(queueName, order, "application/xml");

         ClientResponse res = consumeNext.request().post(String.class);
         Assert.assertEquals(200, res.getStatus());
         Assert.assertEquals("application/xml", res.getHeaders().getFirst("Content-Type").toString().toLowerCase());
         Order order2 = (Order) res.getEntity(Order.class);
         Assert.assertEquals(order, order2);
         consumeNext = MessageTestBase.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "consume-next");
         Assert.assertNotNull(consumeNext);
      }
   }
}