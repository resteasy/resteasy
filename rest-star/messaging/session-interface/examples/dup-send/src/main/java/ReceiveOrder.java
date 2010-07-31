import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.rest.Jms;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReceiveOrder
{
   public static void main(String[] args) throws Exception
   {
      System.out.println("here");
      // first get the create URL for the shipping queue
      ClientRequest request = new ClientRequest("http://localhost:9095/queues/jms.queue.orders");
      ClientResponse res = request.head();
      Link pullConsumers = res.getHeaderAsLink("msg-pull-consumers");
      res = pullConsumers.request().post();
      Link consumeNext = res.getHeaderAsLink("msg-consume-next");
      System.out.println("here2");
      while (true)
      {
         System.out.println("Waiting...");
         res = consumeNext.request().header("Accept-Wait", "10").post();
         if (res.getStatus() == 503)
         {
            System.out.println("Timeout...");
            consumeNext = res.getHeaderAsLink("msg-consume-next");
         }
         else if (res.getStatus() == 200)
         {
            Order order = (Order)res.getEntity(Order.class);
            System.out.println(order);
            consumeNext = res.getHeaderAsLink("msg-consume-next");
         }
         else
         {
            throw new RuntimeException("Failure! " + res.getStatus());
         }
      }
   }
}