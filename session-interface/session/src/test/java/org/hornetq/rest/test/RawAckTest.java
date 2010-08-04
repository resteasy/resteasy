package org.hornetq.rest.test;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

/**
 * Play with HornetQ
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RawAckTest
{
   protected static HornetQServer hornetqServer;
   static ClientSessionFactory sessionFactory;
   static ClientSessionFactory consumerSessionFactory;
   static ClientProducer producer;
   static ClientSession session;

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
      sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));
      consumerSessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));

      hornetqServer.createQueue(new SimpleString("testQueue"), new SimpleString("testQueue"), null, false, false);
      session = sessionFactory.createSession(true, true);
      producer = session.createProducer("testQueue");
      session.start();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      hornetqServer.stop();

   }

   static boolean passed = false;

   private static class MyThread extends Thread
   {
      final ClientConsumer consumer;

      private MyThread(ClientConsumer consumer)
      {
         this.consumer = consumer;
      }

      public void run()
      {
         try
         {
            ClientMessage message = consumer.receiveImmediate();
            int size = message.getBodyBuffer().readInt();
            byte[] bytes = new byte[size];
            message.getBodyBuffer().readBytes(bytes);
            String str = new String(bytes);
            System.out.println(str);
            message.acknowledge();
            message = consumer.receive(1);
            if (message != null)
            {
               System.err.println("Not expecting another message: type=" + message.getType());
               throw new RuntimeException("Failed, receive extra message");
            }
            Assert.assertNull(message);
            passed = true;
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   @Test
   public void testAck() throws Exception
   {

      ClientMessage message;

      message = session.createMessage(ClientMessage.OBJECT_TYPE, false);
      message.getBodyBuffer().writeInt("hello".getBytes().length);
      message.getBodyBuffer().writeBytes("hello".getBytes());
      producer.send(message);

      Thread.sleep(100);

      ClientSession sessionConsumer = sessionFactory.createSession(true, true);
      ClientConsumer consumer = sessionConsumer.createConsumer("testQueue");
      sessionConsumer.start();

      MyThread t = new MyThread(consumer);

      t.start();
      t.join();
      Assert.assertTrue(passed);

      passed = false;

      message = session.createMessage(false);
      message.getBodyBuffer().writeInt("hello2".getBytes().length);
      message.getBodyBuffer().writeBytes("hello2".getBytes());
      producer.send(message);

      Thread.sleep(100);

      t = new MyThread(consumer);

      t.start();
      t.join();
      Assert.assertTrue(passed);

   }
}
