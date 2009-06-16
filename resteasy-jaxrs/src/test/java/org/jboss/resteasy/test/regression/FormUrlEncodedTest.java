package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormUrlEncodedTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/simple")
      @POST
      public StreamingOutput post(@QueryParam("hello") String abs, InputStream entityStream) throws IOException
      {
         Assert.assertNull(abs);
         final InputStream is = entityStream;
         return new StreamingOutput()
         {
            public void write(OutputStream output) throws IOException
            {
               System.out.println("WITHIN STREAMING OUTPUT!!!!");
               int c;
               while ((c = is.read()) != -1)
               {
                  output.write(c);
               }
            }
         };
      }

   }

   @Test
   public void testPost()
   {
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      HttpClient client = new HttpClient();
      {
         PostMethod method = createPostMethod("/simple");
         NameValuePair[] params =
                 {new NameValuePair("hello", "world")};
         method.setRequestBody(params);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            String body = method.getResponseBodyAsString();
            Assert.assertEquals("hello=world", body);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }

}
