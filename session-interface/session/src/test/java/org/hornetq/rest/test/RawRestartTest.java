package org.hornetq.rest.test;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.core.server.JournalType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

/**
 * * Play with HornetQ
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RawRestartTest
{
   protected static HornetQServer hornetqServer;
   static ClientSessionFactory sessionFactory;
   static ClientSessionFactory consumerSessionFactory;
   static ClientProducer producer;
   static ClientSession session;

   @BeforeClass
   public static void setup() throws Exception
   {
      startupTheServer();

      createFactories();

      hornetqServer.createQueue(new SimpleString("testQueue"), new SimpleString("testQueue"), null, true, false);

      session = sessionFactory.createSession(true, true);
      producer = session.createProducer("testQueue");
      session.start();
   }

   private static void createFactories()
   {
      HashMap<String, Object> transportConfig = new HashMap<String, Object>();
      sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));
      consumerSessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName(), transportConfig));
   }

   private static void startupTheServer()
           throws Exception
   {
      Configuration configuration = new ConfigurationImpl();
      configuration.setPersistenceEnabled(false);
      configuration.setSecurityEnabled(false);
      configuration.setJournalType(JournalType.NIO);
      configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

      hornetqServer = HornetQServers.newHornetQServer(configuration);
      hornetqServer.start();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      hornetqServer.stop();

   }

   private static class MyListener implements MessageHandler
   {
      @Override
      public void onMessage(ClientMessage message)
      {
         int size = message.getBodyBuffer().readInt();
         byte[] bytes = new byte[size];
         message.getBodyBuffer().readBytes(bytes);
         String str = new String(bytes);
         System.out.println("Message ID: " + message.getMessageID() + " timestamp: " + message.getTimestamp() + " " + str);

         /*
         try
         {
            message.acknowledge();
         }
         catch (HornetQException e)
         {
            throw new RuntimeException(e);
         }
         */
      }
   }

   @Test
   public void testAck() throws Exception
   {
      if (true) return;  // this test is disabled!

      ClientSession consumer_session = consumerSessionFactory.createSession(false, false);
      ClientConsumer consumer = consumer_session.createConsumer("testQueue");
      consumer.setMessageHandler(new MyListener());
      consumer_session.start();

      ClientMessage message;

      {
         String uuid = UUID.randomUUID().toString();
         System.out.println("Sending: " + uuid);

         message = session.createMessage(ClientMessage.OBJECT_TYPE, true);
         message.getBodyBuffer().writeInt(uuid.getBytes().length);
         message.getBodyBuffer().writeBytes(uuid.getBytes());
         producer.send(message);
      }
      Thread.sleep(1000);
      consumer.close();
      consumer_session.close();
      shutdown();
      startupTheServer();
      createFactories();
      consumer_session = consumerSessionFactory.createSession(false, false);
      consumer = consumer_session.createConsumer("testQueue");
      consumer.setMessageHandler(new MyListener());
      consumer_session.start();
      Thread.sleep(1000);
   }
}
