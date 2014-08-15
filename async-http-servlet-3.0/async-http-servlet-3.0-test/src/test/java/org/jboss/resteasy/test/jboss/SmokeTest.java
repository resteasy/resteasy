package org.jboss.resteasy.test.jboss;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.smoke.Customer;
import org.jboss.resteasy.test.smoke.MyApp;
import org.jboss.resteasy.test.smoke.SimpleResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SmokeTest
{
    private static final String NAME = "async-http";

    @Deployment
    public static WebArchive createDeployment()
    {
       return ShrinkWrap.create(WebArchive.class, NAME + ".war")
             .addClass(Customer.class)
             .addClass(MyApp.class)
             .addClasses(SimpleResource.class)
             .addAsWebInfResource("web.xml")
             ;
    }


   @Test
   public void testFailure() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/" + NAME + "/failure");
         long start = System.currentTimeMillis();
         int status = client.executeMethod(method);
         long end = System.currentTimeMillis() - start;
         assertTrue(end < 1000);
         assertEquals(403, status);
         method.releaseConnection();
      }
   }
   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/" + NAME + "/basic");
         int status = client.executeMethod(method);
         assertEquals(HttpResponseCodes.SC_OK, status);
         assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         // I'm testing unknown content-length here
         GetMethod method = new GetMethod("http://localhost:8080/" + NAME + "/xml");
         int status = client.executeMethod(method);
         assertEquals(HttpResponseCodes.SC_OK, status);
         String result = method.getResponseBodyAsString();
         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         Customer cust = (Customer) ctx.createUnmarshaller().unmarshal(new StringReader(result));
         assertEquals("Bill Burke", cust.getName());
         method.releaseConnection();
      }

   }

   private String readString(InputStream in) throws IOException
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
         GetMethod get = new GetMethod("http://localhost:8080/" + NAME + "/gzip");
         get.addRequestHeader("Accept-Encoding", "gzip, deflate");
         int status = client.executeMethod(get);
         assertEquals(200, status);
         assertEquals("gzip", get.getResponseHeader("Content-Encoding").getValue());
         GZIPInputStream gzip = new GZIPInputStream(get.getResponseBodyAsStream());
         String response = readString(gzip);


         // test that it is actually zipped
         assertEquals(response, "HELLO WORLD");
      }

   }

}
