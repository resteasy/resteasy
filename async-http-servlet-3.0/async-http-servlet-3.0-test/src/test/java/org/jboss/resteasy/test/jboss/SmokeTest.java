package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.smoke.Customer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SmokeTest
{

   @Test
   public void testFailure() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/failure");
         long start = System.currentTimeMillis();
         int status = client.executeMethod(method);
         long end = System.currentTimeMillis() - start;
         Assert.assertTrue(end < 1000);
         Assert.assertEquals(403, status);
         method.releaseConnection();
      }
   }
   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         // I'm testing unknown content-length here
         GetMethod method = new GetMethod("http://localhost:8080/xml");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         String result = method.getResponseBodyAsString();
         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         Customer cust = (Customer) ctx.createUnmarshaller().unmarshal(new StringReader(result));
         Assert.assertEquals("Bill Burke", cust.getName());
         method.releaseConnection();
      }

   }

   public String readString(InputStream in) throws IOException
   {
      char[] buffer = new char[1024];
      StringBuilder builder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int wasRead = 0;
      do
      {
         wasRead = reader.read(buffer, 0, 1024);
         if (wasRead > 0)
         {
            builder.append(buffer, 0, wasRead);
         }
      }
      while (wasRead > -1);

      return builder.toString();
   }


   @Test
   public void testGzip() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         GetMethod get = new GetMethod("http://localhost:8080/gzip");
         get.addRequestHeader("Accept-Encoding", "gzip, deflate");
         int status = client.executeMethod(get);
         Assert.assertEquals(200, status);
         Assert.assertEquals("gzip", get.getResponseHeader("Content-Encoding").getValue());
         GZIPInputStream gzip = new GZIPInputStream(get.getResponseBodyAsStream());
         String response = readString(gzip);


         // test that it is actually zipped
         Assert.assertEquals(response, "HELLO WORLD");
      }

   }

}
