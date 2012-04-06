package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
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

import static org.jboss.resteasy.test.TestPortProvider.*;

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
      ClientRequest request = new ClientRequest(generateURL("/simple"));
      request.formParameter("hello", "world");
      try
      {
         ClientResponse<String> response = request.post(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String body = response.getEntity();
         Assert.assertEquals("hello=world", body);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
