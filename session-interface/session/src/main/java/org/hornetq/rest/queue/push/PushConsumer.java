package org.hornetq.rest.queue.push;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.core.logging.Logger;
import org.hornetq.rest.queue.push.xml.PushRegistration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushConsumer implements MessageHandler
{
   private static final Logger log = Logger.getLogger(PushConsumer.class);
   private PushRegistration registration;
   protected ClientSessionFactory factory;
   protected ClientSession session;
   protected ClientConsumer consumer;
   protected String destination;
   protected String id;
   protected PushStrategy strategy;

   public PushConsumer(ClientSessionFactory factory, String destination, String id, PushRegistration registration)
   {
      this.factory = factory;
      this.destination = destination;
      this.id = id;
      this.registration = registration;
   }

   public PushRegistration getRegistration()
   {
      return registration;
   }

   public String getDestination()
   {
      return destination;
   }

   public void start() throws Exception
   {
      if (registration.getTarget().getClassName() != null)
      {
         Class clazz = Thread.currentThread().getContextClassLoader().loadClass(registration.getTarget().getClassName());
         strategy = (PushStrategy) clazz.newInstance();
      }
      else if (registration.getTarget().getRelationship() != null)
      {
         if (registration.getTarget().getRelationship().equals("destination"))
         {
            strategy = new HornetQPushStrategy();
         }
         else if (registration.getTarget().getRelationship().equals("template"))
         {
            strategy = new UriTemplateStrategy();
         }
      }
      if (strategy == null)
      {
         strategy = new UriStrategy();
      }
      strategy.setRegistration(registration);
      strategy.start();

      session = factory.createSession(false, false);
      consumer = session.createConsumer(destination);
      consumer.setMessageHandler(this);
      session.start();
      log.info("Push consumer started for: " + registration.getTarget());
   }

   public void stop()
   {
      try
      {
         if (consumer != null) consumer.close();
      }
      catch (HornetQException e)
      {
      }
      try
      {
         if (session != null) session.close();
      }
      catch (HornetQException e)
      {

      }
      try
      {
         if (strategy != null) strategy.stop();
      }
      catch (Exception e)
      {
      }
   }

   @Override
   public void onMessage(ClientMessage clientMessage)
   {
      if (strategy.push(clientMessage) == false)
      {
         throw new RuntimeException("Failed to push message to " + registration.getTarget());
      }
      else
      {
         try
         {
            log.info("Acknowledging: " + clientMessage.getMessageID());
            clientMessage.acknowledge();
         }
         catch (HornetQException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}
