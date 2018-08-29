package org.jboss.resteasy.test;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamingOutputTest
{
   private static final Logger LOG = Logger.getLogger(StreamingOutputTest.class);
   static String BASE_URI = generateURL("");
   static Client client;
   static CountDownLatch latch;

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
      @GET
      @Path("delay")
      @Produces(MediaType.TEXT_PLAIN)
      public StreamingOutput delay() {
         return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
               for (int i = 0; i < 10; i++) {
                  output.write(("" + i + "\n\n").getBytes(StandardCharsets.ISO_8859_1));
                  try
                  {
                     latch.countDown();
                     Thread.sleep(100);
                  }
                  catch (InterruptedException e)
                  {
                     throw new RuntimeException(e);
                  }
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
      client = new ResteasyClientBuilder().connectionPoolSize(10).build();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      NettyContainer.stop();
   }

   static boolean pass = false;

   @Test
   public void testConcurrent() throws Exception
   {
      pass = false;
      latch = new CountDownLatch(1);
      Runnable r = new Runnable()
      {
         @Override
         public void run()
         {
            String str = client.target(BASE_URI).path("test/delay").request().get(String.class);
            pass = true;
         }
      };
      Thread t = new Thread(r);
      t.start();
      latch.await();
      long start = System.currentTimeMillis();
      testStreamingOutput();
      long end = System.currentTimeMillis() - start;
      LOG.info(end);
      Assert.assertTrue(end < 1000);
      t.join();
      Assert.assertTrue(pass);
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
