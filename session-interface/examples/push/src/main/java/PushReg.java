import org.hornetq.rest.queue.push.xml.Authentication;
import org.hornetq.rest.queue.push.xml.BasicAuth;
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
      // get the push consumers factory resource
      ClientRequest request = new ClientRequest("http://localhost:9095/queues/jms.queue.orders");
      ClientResponse res = request.head();
      Link pushConsumers = res.getHeaderAsLink("msg-push-consumers");

      // next create the XML document that represents the registration
      // Really, just create a link with the shipping URL and the type you want posted
      PushRegistration reg = new PushRegistration();
      BasicAuth authType = new BasicAuth();
      authType.setUsername("guest");
      authType.setPassword("guest");
      Authentication auth = new Authentication();
      auth.setType(authType);
      reg.setAuthenticationMechanism(auth);
      XmlLink target = new XmlLink();
      target.setHref("http://localhost:9095/queues/jms.queue.shipping");
      target.setType("application/xml");
      target.setRelationship("destination");
      reg.setTarget(target);

      res = pushConsumers.request().body("application/xml", reg).post();
      System.out.println("Create push registration.  Resource URL: " + res.getLocation().getHref());
   }
}
