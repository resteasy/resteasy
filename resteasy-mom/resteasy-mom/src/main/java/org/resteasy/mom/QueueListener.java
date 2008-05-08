package org.resteasy.mom;

import javax.jms.Connection;
import javax.jms.Destination;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueListener extends Listener
{
   public QueueListener(Destination destination, Connection connection, String callback, MessageProcessor processor, DlqProcessor dlq, String selector)
           throws Exception
   {
      super(destination, connection, callback, processor, dlq);

      System.out.println("Callback URI: " + callback);
      System.out.println("SELECTOR : " + selector);
      consumer = session.createConsumer(destination, selector);
      consumer.setMessageListener(this);
      connection.start();
   }
}
