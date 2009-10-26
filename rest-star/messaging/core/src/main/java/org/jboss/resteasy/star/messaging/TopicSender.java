package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicSender
{
   private TopicMessageRepository repository;
   private ClientSessionFactory sessionFactory;
   private String destination;

   public Message post(MultivaluedMap<String, String> headers, byte[] body) throws Exception
   {
      Message msg = repository.addMessage(headers, body);
      ClientSession session = sessionFactory.createSession();
      try
      {
         ClientProducer producer = session.createProducer(destination);
         ClientMessage message = session.createClientMessage(false);
         message.putLongProperty("m-id", msg.getId());
         System.out.println("sending message: " + msg.getId());
         producer.send(message);
         session.start();
         return msg;
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
