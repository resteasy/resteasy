import org.hornetq.jms.client.HornetQDestination;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;

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
      // first get the create URL for the shipping queue
      ClientRequest request = new ClientRequest("http://localhost:9095/queues/jms.queue.orders");
      ClientResponse res = request.head();
      Link create = res.getHeaderAsLink("msg-create");

      System.out.println("Send Bill's order...");
      Order order = new Order();
      order.setName("Bill");
      order.setItem("iPhone4");
      order.setAmount("$199.99");

      res = create.request().body("application/xml", order).post();

      if (res.getStatus() == 307)
      {
         Link redirect = res.getLocation();
         res = redirect.request().body("application/xml", order).post();
      }

      if (res.getStatus() != 201) throw new RuntimeException("Failed to post");

      create = res.getHeaderAsLink("msg-create-next");

      if (res.getStatus() != 201) throw new RuntimeException("Failed to post");
      
      System.out.println("Send Monica's order...");
      order.setName("Monica");

      res = create.request().body("application/xml", order).post();

      if (res.getStatus() != 201) throw new RuntimeException("Failed to post");

      System.out.println("Resend Monica's order over same create-next link...");

      res = create.request().body("application/xml", order).post();

      if (res.getStatus() != 201) throw new RuntimeException("Failed to post");
   }
}
