package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.smoke.Customer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.StringReader;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SmokeTest
{
   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/async-http-servlet-3.0-test/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         // I'm testing unknown content-length here
         GetMethod method = new GetMethod("http://localhost:8080/async-http-servlet-3.0-test/xml");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         String result = method.getResponseBodyAsString();
         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         Customer cust = (Customer) ctx.createUnmarshaller().unmarshal(new StringReader(result));
         Assert.assertEquals("Bill Burke", cust.getName());
         method.releaseConnection();
      }

   }

}
