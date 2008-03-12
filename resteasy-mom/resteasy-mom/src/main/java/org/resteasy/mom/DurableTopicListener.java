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

   public DurableTopicListener(String name, Destination destination, Connection connection, String callback, MessageProcessor processor, String selector)
           throws Exception
   {
      super(destination, connection, callback, processor);
      this.name = name;
      this.callback = name;
      System.out.println("Callback URI: " + callback);
      System.out.println("SELECTOR : " + selector);
      consumer = session.createDurableSubscriber((Topic) destination, name, selector, false);
      consumer.setMessageListener(this);
      connection.start();
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