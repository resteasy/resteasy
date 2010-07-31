import org.hornetq.rest.queue.push.xml.PushRegistration;
import org.hornetq.rest.queue.push.xml.XmlLink;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushReg
{
   public static void main(String[] args) throws Exception
   {
      // first get the create URL for the shipping queue
      ClientRequest request = new ClientRequest("http://localhost:9095/queues/jms.queue.shipping");
      ClientResponse res = request.head();
      String createShippingUrl = (String)res.getHeaders().getFirst("msg-create");

      // next get the push consumers factory resource
      request = new ClientRequest("http://localhost:9095/queues/jms.queue.orders");
      res = request.head();
      Link pushConsumers = res.getHeaderAsLink("msg-push-consumers");

      // next create the XML document that represents the registration
      // Really, just create a link with the shipping URL and the type you want posted
      PushRegistration reg = new PushRegistration();
      XmlLink target = new XmlLink();
      target.setHref(createShippingUrl);
      target.setType("application/xml");
      target.setRelationship("create");
      reg.setTarget(target);

      res = pushConsumers.request().body("application/xml", reg).post();
      System.out.println("Create push registration.  Resource URL: " + res.getLocation().getHref());
   }
}
