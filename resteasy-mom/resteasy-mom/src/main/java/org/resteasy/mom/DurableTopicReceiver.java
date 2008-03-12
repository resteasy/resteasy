package org.resteasy.mom;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DurableTopicReceiver extends Receiver
{
   private String name;

   public DurableTopicReceiver(String name, Connection connection, Destination destination, MessageProcessor processor, String selector) throws Exception
   {
      super(connection, selector, destination, processor);
      this.name = name;
      getConsumer();
   }

   protected void createConsumer()
           throws JMSException
   {
      consumer = session.createDurableSubscriber((Topic) destination, name, selector, false);
   }

   @Override
   public void close()
   {
      throw new RuntimeException("Illegal to call this method, call close(boolean unsubscribe)");
   }

   public synchronized void close(boolean unsubscribe)
   {
      try
      {
         connection.stop();
      }
      catch (JMSException e)
      {
         throw new RuntimeException(e);
      }
      try
      {
         if (consumer != null) consumer.close();
      }
      catch (JMSException ignored) {}
      consumer = null;

      try
      {
         if (unsubscribe)
         {
            session.unsubscribe(name);
         }

         if (session != null) session.close();
      }
      catch (JMSException ignore) {}
      session = null;
      try
      {
         if (connection != null) connection.close();
         connection = null;
      }
      catch (JMSException ignored)
      {
      }
   }

}