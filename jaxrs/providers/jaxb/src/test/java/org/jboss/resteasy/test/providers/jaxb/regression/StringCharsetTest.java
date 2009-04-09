package org.jboss.resteasy.test.providers.jaxb.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringCharsetTest extends BaseResourceTest
{
   @Path("/charset")
   public static class CharsetService
   {
      @GET
      @Path("test.xml")
      @Produces("application/xml")
      public RespondTest getTestXML()
      {
         String test = "Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269;
         return new RespondTest(test);
      }

      @GET
      @Path("test.json")
      @Produces("application/json;charset=UTF-8")
      public RespondTest getTestJSON()
      {
         return new RespondTest("Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269);
      }

      @GET
      @Path("test.html")
      @Produces("text/html;charset=UTF-8")
      public String getTestHTML()
      {
         return new String("<html><body>Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269 + "</body></html>");
      }

      @GET
      @Path("test_stream.html")
      @Produces("text/html;charset=UTF-8")
      public StreamingOutput getTestStream()
      {
         return new StreamingOutput()
         {
            public void write(OutputStream outputStream) throws IOException, WebApplicationException
            {
               PrintStream writer = new PrintStream(outputStream, true, "UTF-8");
               writer.println("<html><body>Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269 + "</body></html>");
            }
         };
      }

   }


   @XmlRootElement(name = "respond_test")
   public static class RespondTest
   {
      protected String word;

      public RespondTest()
      {

      }

      public RespondTest(String _word)
      {
         this.word = _word;
      }

      @XmlElement(name = "word")
      public String getWord()
      {
         return word;
      }

      public void setWord(String word)
      {
         this.word = word;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(CharsetService.class);
   }

   @Test
   public void testIt() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(generateURL("/charset/test.xml"));
      method.addRequestHeader("Accept", "application/xml;charset=iso-8859-2");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      String str = new String(method.getResponseBody(), "UTF-8");
      System.out.println(str);

   }


}
