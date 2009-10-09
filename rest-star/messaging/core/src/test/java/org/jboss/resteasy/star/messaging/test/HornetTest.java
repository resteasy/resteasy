package org.jboss.resteasy.star.messaging.test;

import org.junit.Test;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQ;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientConsumer;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;

import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HornetTest
{
   @Test
   public void testIt() throws Exception
   {
      try
      {

         // Step 1. Create the Configuration, and set the properties accordingly
         Configuration configuration = new ConfigurationImpl();
         configuration.setPersistenceEnabled(false);
         configuration.setSecurityEnabled(false);
         configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

         // Step 2. Create and start the server
         HornetQServer server = HornetQ.newHornetQServer(configuration);
         server.start();


         // Step 3. As we are not using a JNDI environment we instantiate the objects directly
         ClientSessionFactory sf = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));

         // Step 4. Create a core queue
         ClientSession coreSession = sf.createSession(false, false, false);

         final String queueName = "queue.exampleQueue";

         coreSession.createQueue(queueName, queueName, true);

         coreSession.close();

         ClientSession session = null;

         try
         {

            // Step 5. Create the session, and producer
            session = sf.createSession();

            ClientProducer producer = session.createProducer(queueName);

            // Step 6. Create and send a message
            ClientMessage message = session.createClientMessage(false);

            final String propName = "myprop";

            message.putStringProperty(propName, "Hello sent at " + new Date());

            System.out.println("Sending the message.");

            producer.send(message);

            session.close();

            session = sf.createSession();

            // Step 7. Create the message consumer and start the connection
            ClientConsumer messageConsumer = session.createConsumer(queueName);
            session.start();

            // Step 8. Receive the message.
            ClientMessage messageReceived = messageConsumer.receive(1000);
            System.out.println("Received TextMessage:" + messageReceived.getProperty(propName));
         }
         finally
         {
            // Step 9. Be sure to close our resources!
            if (session != null)
            {
               session.close();
            }

            // Step 10. Stop the server
            server.stop();
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }
}
