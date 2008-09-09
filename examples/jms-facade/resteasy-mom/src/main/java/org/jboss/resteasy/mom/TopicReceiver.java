package org.jboss.resteasy.mom;

import javax.jms.Connection;
import javax.jms.Destination;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicReceiver extends Receiver
{
   public TopicReceiver(Connection connection, Destination destination, MessageProcessor processor, String selector) throws Exception
   {
      super(connection, selector, destination, processor);
      getConsumer();
   }
}
