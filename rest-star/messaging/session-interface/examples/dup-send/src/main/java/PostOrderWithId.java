import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostOrderWithId
{
   public static void main(String[] args) throws Exception
   {
      if (args.length < 1 || args[0] == null) throw new RuntimeException("You must pass in a parameter");

      // first get the create URL for the shipping queue
      ClientRequest request = new ClientRequest("http://localhost:9095/queues/jms.queue.orders");
      ClientResponse res = request.head();
      Link create = res.getHeaderAsLink("msg-create-with-id");


      Order order = new Order();
      order.setName(args[0]);
      order.setItem("iPhone4");
      order.setAmount("$199.99");

      res = create.request().pathParameter("id", args[0]).body("application/xml", order).post();

      if (res.getStatus() != 201) throw new RuntimeException("Failed to post");

      System.out.println("Sent order " + args[0]);
   }
}
