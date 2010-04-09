package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicSequencer implements MessageHandler
{
   private String incoming;
   private CurrentTopicIndex current;
   private TopicMessageRepository repository;
   private ClientSessionFactory factory;
   private ClientSession session;

   public String getIncoming()
   {
      return incoming;
   }

   public void setIncoming(String incoming)
   {
      this.incoming = incoming;
   }

   public CurrentTopicIndex getCurrent()
   {
      return current;
   }

   public void setCurrent(CurrentTopicIndex current)
   {
      this.current = current;
   }

   public TopicMessageRepository getRepository()
   {
      return repository;
   }

   public void setRepository(TopicMessageRepository repository)
   {
      this.repository = repository;
   }

   public ClientSessionFactory getFactory()
   {
      return factory;
   }

   public void setFactory(ClientSessionFactory factory)
   {
      this.factory = factory;
   }

   public void start() throws Exception
   {
      session = factory.createSession();
      session.createConsumer(incoming).setMessageHandler(this);
      session.start();
      System.out.println("initialized and started consumer for " + incoming);
   }

   public void onMessage(ClientMessage notification)
   {
      String id = notification.getStringProperty("m-id");
      System.out.println("Received message: " + id);
      TopicMessageIndex top = current.getCurrent();
      TopicMessageIndex newIndex = repository.addIndex(id);
      newIndex.setLatch(new CountDownLatch(1));
      top.setNext(id);
      current.setCurrent(newIndex);
      top.getLatch().countDown();
      try
      {
         notification.acknowledge();
      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void stop()
   {
      try
      {
         session.close();
      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
   }
}
