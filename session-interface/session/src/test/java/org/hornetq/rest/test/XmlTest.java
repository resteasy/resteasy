package org.hornetq.rest.test;

import org.hornetq.rest.queue.push.xml.PushRegistration;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.StringReader;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class XmlTest
{
   @Test
   public void testPush() throws Exception
   {
      String xml = "<push-registration id=\"111\">\n" +
              "   <destination>jms.queue.bar</destination>\n" +
              "   <durable>true</durable>\n" +
              "   <link rel=\"template\" href=\"http://somewhere.com/resources/{id}/messages\" method=\"PUT\"/>\n" +
              "   <authentication>\n" +
              "      <basic-auth><username>guest</username><password>geheim</password></basic-auth>" +
              "   </authentication>\n" +
              "   <header name=\"foo\">bar</header>" +
              "</push-registration>";

      JAXBContext ctx = JAXBContext.newInstance(PushRegistration.class);
      PushRegistration reg = (PushRegistration) ctx.createUnmarshaller().unmarshal(new StringReader(xml));

      System.out.println(reg);
   }
}
