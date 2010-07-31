import org.hornetq.jms.client.HornetQDestination;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostOrder
{
   public static void main(String[] args) throws Exception
   {
      ConnectionFactory factory = JmsHelper.createConnectionFactory("hornetq-client.xml");
      Destination destination = (HornetQDestination) HornetQDestination.fromAddress("jms.queue.orders");

      Connection conn = factory.createConnection();
      try
      {
         Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageProducer producer = session.createProducer(destination);
         ObjectMessage message = session.createObjectMessage();

         Order order = new Order("Bill", "$199.99", "iPhone4");
         message.setObject(order);
         producer.send(message);
      }
      finally
      {
         conn.close();
      }
   }
}
