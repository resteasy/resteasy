package org.hornetq.rest.test;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.remoting.impl.invm.TransportConstants;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.rest.HttpHeaderProperty;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JmsSelectorTest
{
   public static ConnectionFactory connectionFactory;
   public static HornetQServer hornetqServer;
   public static ClientSessionFactory sessionFactory;
   public static String queueName = HornetQDestination.createQueueAddressFromName("testQueue").toString();


   @BeforeClass
   public static void setup() throws Exception
   {
      Configuration configuration = new ConfigurationImpl();
      configuration.setPersistenceEnabled(false);
      configuration.setSecurityEnabled(false);
      configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

      hornetqServer = HornetQServers.newHornetQServer(configuration);
      hornetqServer.start();
      HashMap<String, Object> transportConfig = new HashMap<String, Object>();
      transportConfig.put(TransportConstants.SERVER_ID_PROP_NAME, "0");
      sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));
      connectionFactory = new HornetQConnectionFactory(sessionFactory);
      ClientSession session = sessionFactory.createSession(false, false, false);
      session.createQueue(queueName, queueName, false);
      session.close();
   }

   @AfterClass
   public static void shutdownHornetqServerAndManager() throws Exception
   {
      hornetqServer.stop();
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
      public String toString()
      {
         return "Order{" +
                 "name='" + name + '\'' +
                 '}';
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

   public static void publish(String dest, Serializable object, String contentType, String tag) throws Exception
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
         if (tag != null)
         {
            message.setStringProperty("MyTag", tag);
         }
         message.setObject(object);

         producer.send(message);
      }
      finally
      {
         conn.close();
      }
   }

   @Test
   public void testDummy()
   {
      
   }


   //@Test
   public void testJmsProducer() throws Exception
   {
      System.out.println("Queue name: " + queueName);
      Destination dest = createDestination(queueName);

      Connection conn = connectionFactory.createConnection();

      Session sessionOne = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
      Session sessionTwo = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
      MessageConsumer consumerOne = sessionOne.createConsumer(dest, "MyTag = '1'");
      MessageConsumer consumerTwo = sessionTwo.createConsumer(dest, "MyTag = '2'");
      conn.start();


      {
         Order order = new Order();
         order.setName("1");
         order.setAmount("$5.00");
         publish(queueName, order, null, "1");
         order.setName("2");
         publish(queueName, order, null, "1");
         order.setName("3");
         publish(queueName, order, null, "1");
         order.setName("4");
         publish(queueName, order, null, "2");
         order.setName("5");
         publish(queueName, order, null, "2");

         {
            order.setName("1");
            consumeOrder(order, consumerOne);
            order.setName("2");
            consumeOrder(order, consumerOne);
            order.setName("3");
            consumeOrder(order, consumerOne);
            order.setName("4");
            consumeOrder(order, consumerTwo);
            order.setName("5");
            consumeOrder(order, consumerTwo);
         }
      }
   }

   private void consumeOrder(Order order, MessageConsumer consumer)
           throws Exception
   {
      ObjectMessage message = (ObjectMessage) consumer.receive(2000);
      Assert.assertNotNull(message);
      message.acknowledge();
      Assert.assertNotNull(message);
      Order order2 = (Order) message.getObject();
      Assert.assertEquals(order, order2);
   }
}