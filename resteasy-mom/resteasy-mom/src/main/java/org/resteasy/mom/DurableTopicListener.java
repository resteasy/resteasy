package org.resteasy.mom;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DurableTopicListener extends Listener
{
   private String name;

   public DurableTopicListener(String name, Destination destination, Connection connection, String callback, MessageProcessor processor, DlqProcessor dlq, String selector)
           throws Exception
   {
      super(destination, connection, callback, processor, dlq);
      this.name = name;
      consumer = session.createDurableSubscriber((Topic) destination, name, selector, false);
      consumer.setMessageListener(this);
      connection.start();
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