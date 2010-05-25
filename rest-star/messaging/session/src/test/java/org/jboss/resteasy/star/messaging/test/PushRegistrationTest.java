package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.star.messaging.queue.push.xml.Authentication;
import org.jboss.resteasy.star.messaging.queue.push.xml.BasicAuth;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;
import org.jboss.resteasy.star.messaging.queue.push.xml.XmlLink;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.StringWriter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushRegistrationTest
{
   @Test
   public void testXml() throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(PushRegistration.class);

      PushRegistration reg = new PushRegistration();
      XmlLink link = new XmlLink();
      link.setHref("http://foo.bar");
      link.setRelationship("next");
      reg.setTarget(link);
      BasicAuth basic = new BasicAuth();
      basic.setUsername("bill");
      basic.setPassword("password");
      Authentication auth = new Authentication();
      auth.setType(basic);
      reg.setAuthenticationMechanism(auth);

      StringWriter writer = new StringWriter();
      ctx.createMarshaller().marshal(reg, writer);
      System.out.println(writer);
   }
}
