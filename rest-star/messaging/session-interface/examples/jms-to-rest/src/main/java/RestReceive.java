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
      res = pullConsumers.request().formParameter("autoAck", "false").post();
      Link ackNext = res.getHeaderAsLink("msg-acknowledge-next");
      while (true)
      {
         System.out.println("Waiting...");
         res = ackNext.request()
                 .header("Accept-Wait", "10")
                 .header("Accept", "application/xml")
                 .post();
         if (res.getStatus() == 503)
         {
            System.out.println("Timeout...");
            ackNext = res.getHeaderAsLink("msg-acknowledge-next");
         }
         else if (res.getStatus() == 200)
         {
            Order order = (Order) res.getEntity(Order.class);
            System.out.println(order);
            Link ack = res.getHeaderAsLink("msg-acknowledgement");
            res = ack.request().formParameter("acknowledge", "true").post();
            ackNext = res.getHeaderAsLink("msg-acknowledge-next");
         }
         else
         {
            throw new RuntimeException("Failure! " + res.getStatus());
         }
      }
   }
}