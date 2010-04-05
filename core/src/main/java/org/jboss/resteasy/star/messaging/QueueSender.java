package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueSender
{
   private QueueMessageRepository repository;
   private ClientSessionFactory sessionFactory;
   private String destination;

   public Message post(MultivaluedMap<String, String> headers, byte[] body) throws Exception
   {
      Message msg = repository.addMessage(headers, body);
      ClientSession session = sessionFactory.createSession();
      try
      {
         ClientProducer producer = session.createProducer(destination);
         ClientMessage message = session.createMessage(false);
         message.putLongProperty("m-id", msg.getId());
         System.out.println("sending to message queue: " + msg.getId());
         producer.send(message);
         session.start();
         return msg;
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