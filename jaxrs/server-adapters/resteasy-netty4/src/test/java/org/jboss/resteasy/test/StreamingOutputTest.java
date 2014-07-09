package org.jboss.resteasy.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamingOutputTest
{
   static String BASE_URI = generateURL("");
   static Client client;

   @Path("/test")
   public static class Resteasy1029Netty4StreamingOutput {

      @GET
      @Produces(MediaType.TEXT_PLAIN)
      public StreamingOutput stream() {
         return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
               for (int i = 0; i < 10; i++) {
                  output.write(("" + i + "\n\n").getBytes(StandardCharsets.ISO_8859_1));
                  output.flush();
               }
               output.close();
            }
         };
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      NettyContainer.start().getRegistry().addPerRequestResource(Resteasy1029Netty4StreamingOutput.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      NettyContainer.stop();
   }

   @Test
   public void testStreamingOutput() throws Exception
   {
      Response response = client.target(BASE_URI).path("test").request().get();
      Assert.assertTrue(response.readEntity(String.class).equals("0\n" +
              "\n1\n" +
              "\n2\n" +
              "\n3\n" +
              "\n4\n" +
              "\n5\n" +
              "\n6\n" +
              "\n7\n" +
              "\n8\n" +
              "\n9\n" +
              "\n"));
      Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
   }
}
