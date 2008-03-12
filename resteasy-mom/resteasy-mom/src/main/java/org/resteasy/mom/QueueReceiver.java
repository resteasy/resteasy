package org.resteasy.mom;

import javax.jms.Connection;
import javax.jms.Destination;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueReceiver extends Receiver
{

   public QueueReceiver(Destination destination, Connection connection, MessageProcessor processor, String selector) throws Exception
   {
      super(connection, selector, destination, processor);
   }

}
