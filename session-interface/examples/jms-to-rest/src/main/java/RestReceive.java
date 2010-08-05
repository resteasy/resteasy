import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RestReceive
{
   public static void main(String[] args) throws Exception
   {
      // first get the create URL for the shipping queue
      ClientRequest request = new ClientRequest("http://localhost:9095/queues/jms.queue.orders");
      ClientResponse res = request.head();
      Link pullConsumers = res.getHeaderAsLink("msg-pull-consumers");
      res = pullConsumers.request().post();
      Link consumeNext = res.getHeaderAsLink("msg-consume-next");
      while (true)
      {
         System.out.println("Waiting...");
         res = consumeNext.request()
                 .header("Accept-Wait", "10")
                 .header("Accept", "application/xml")
                 .post();
         if (res.getStatus() == 503)
         {
            System.out.println("Timeout...");
            consumeNext = res.getHeaderAsLink("msg-consume-next");
         }
         else if (res.getStatus() == 200)
         {
            Order order = (Order) res.getEntity(Order.class);
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