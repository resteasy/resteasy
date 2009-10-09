package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientConsumer;
import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.exception.HornetQException;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Sequencer implements Runnable
{
   private String incoming;
   private String outgoing;
   private CurrentMessageIndex current;
   private MessageRepository repository;
   private volatile boolean shutdown;
   private ClientSessionFactory factory;

   public String getIncoming()
   {
      return incoming;
   }

   public void setIncoming(String incoming)
   {
      this.incoming = incoming;
   }

   public String getOutgoing()
   {
      return outgoing;
   }

   public void setOutgoing(String outgoing)
   {
      this.outgoing = outgoing;
   }

   public CurrentMessageIndex getCurrent()
   {
      return current;
   }

   public void setCurrent(CurrentMessageIndex current)
   {
      this.current = current;
   }

   public MessageRepository getRepository()
   {
      return repository;
   }

   public void setRepository(MessageRepository repository)
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

   public void run()
   {
      ClientConsumer consumer = null;
      ClientProducer producer = null;
      ClientSession session = null;
      try
      {
         session = factory.createSession();
         consumer = session.createConsumer(incoming);
         producer = null;
         if (outgoing != null) producer = session.createProducer(outgoing);
         session.start();
         System.out.println("initialized and started consumer for " + incoming);
      }
      catch (HornetQException e)
      {
         e.printStackTrace();
      }
      while (shutdown == false)
      {
         try
         {
            System.out.println("Blocking...");
            ClientMessage notification = consumer.receive();
            long id = (Long) notification.getProperty("m-id");
            System.out.println("Received message: " + id);
            MessageIndex top = current.getCurrent();
            MessageIndex newIndex = repository.addIndex(id);
            newIndex.setLatch(new CountDownLatch(1));
            top.setNext(id);
            current.setCurrent(newIndex);
            if (producer != null) producer.send(notification);
            top.getLatch().countDown();
            notification.acknowledge();
         }
         catch (Exception e)
         {
            System.err.println("Exception thrown in Sequencer: " + e.getMessage());
            e.printStackTrace();
         }
      }
      try
      {
         session.close();
      }
      catch (Exception e)
      {
         System.err.println("Exception thrown in Sequencer: " + e.getMessage());
         e.printStackTrace();
      }
   }

   public void shutdown()
   {
      shutdown = true;
   }
}
