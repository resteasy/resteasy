package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.Message;
import org.jboss.resteasy.star.messaging.MessagePublisher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicPublisher implements MessagePublisher
{
   private TopicMessageRepository repository;
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
         System.out.println("sending message: " + msg.getId());
         producer.send(message);
         session.start();
      }
      finally
      {
         session.close();
      }

   }

   public TopicMessageRepository getRepository()
   {
      return repository;
   }

   public void setRepository(TopicMessageRepository repository)
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
