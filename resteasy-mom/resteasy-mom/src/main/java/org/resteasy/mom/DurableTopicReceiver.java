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

   public void unsubscribe()
   {
      try
      {
         session.unsubscribe(name);
      }
      catch (JMSException ignored)
      {
      }
   }

   public void close()
   {
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