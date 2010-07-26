package org.hornetq.rest.queue;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.rest.Message;
import org.hornetq.rest.MessagePublisher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueuePublisher implements MessagePublisher
{
   private QueueMessageRepository repository;
   private ClientSessionFactory sessionFactory;
   private String destination;

   public void publish(Message msg) throws Exception
   {
      ClientSession session = sessionFactory.createSession();
      try
      {
         ClientProducer producer = session.createProducer(destination);
         ClientMessage message = session.createMessage(false);
         message.putStringProperty("m-id", msg.getId());
         System.out.println("sending to message queue: " + msg.getId());
         producer.send(message);
         session.start();
      }
      finally
      {
         session.close();
      }

   }

   public QueueMessageRepository getRepository()
   {
      return repository;
   }

   public void setRepository(QueueMessageRepository repository)
   {
      this.repository = repository;
   }

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }
}