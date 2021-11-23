package org.jboss.resteasy.test;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.jboss.resteasy.spi.AsyncStreamingOutput;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamingOutputTest
{
   private static final int LOOP_COUNT = 10;
   static String BASE_URI = generateURL("");
   static Client client;
   static CountDownLatch latch;

   @Path("/org/jboss/resteasy/test")
   public static class Resteasy1029Netty4StreamingOutput {

      @GET
      @Produces(MediaType.TEXT_PLAIN)
      public StreamingOutput stream() {
         return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
               for (int i = 0; i < LOOP_COUNT; i++) {
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
               for (int i = 0; i < LOOP_COUNT; i++) {
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

      @POST
      @Path("/largeContent")
      @Produces(MediaType.TEXT_PLAIN)
      public StreamingOutput largeContent(final InputStream in) {
         return out -> IOUtils.copy(in, out);
      }

      @POST
      @Path("/async/largeContent")
      @Produces(MediaType.TEXT_PLAIN)
      public AsyncStreamingOutput asyncLargeContent(final InputStream in) {
         final BiConsumer<InputStream, OutputStream> writeFunction = (input, output) -> {
            try {
               IOUtils.copy(input, output);
            } catch (final IOException e) {
               throw new RuntimeException(e);
            }
         };
         return out -> CompletableFuture.runAsync(() -> writeFunction.accept(in, out));
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      ReactorNettyContainer.start().getRegistry().addPerRequestResource(Resteasy1029Netty4StreamingOutput.class);
      client = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(10).build();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      ReactorNettyContainer.stop();
   }

   static boolean pass = false;

   @Test
   public void testConcurrent() throws Exception
   {
      pass = false;
      latch = new CountDownLatch(LOOP_COUNT);
      Runnable r = new Runnable()
      {
         @Override
         public void run()
         {
            String str = client.target(BASE_URI).path("org/jboss/resteasy/test/delay").request().get(String.class);
            pass = true;
         }
      };
      Thread t = new Thread(r);
      t.start();
      Assert.assertTrue("Result not returned within 30 seconds. Lath count: " + latch.getCount(), latch.await(30, TimeUnit.SECONDS));
      long start = System.currentTimeMillis();
      testStreamingOutput();
      long end = System.currentTimeMillis() - start;
      Assert.assertTrue(end < 1000);
      t.join();
      Assert.assertTrue(pass);
   }

   @Test
   public void testStreamingOutput() throws Exception
   {
      Response response = client.target(BASE_URI).path("org/jboss/resteasy/test").request().get();
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

   @Test
   public void testStreamingOutputForLargeContent()
   {
      testStreamingLargeContent("/org/jboss/resteasy/test/largeContent");
   }

   @Test
   public void testAsyncStreamingOutputForLargeContent()
   {
      testStreamingLargeContent("/org/jboss/resteasy/test/async/largeContent");
   }

   private void testStreamingLargeContent(final String path) {
      final Random random = new Random();
      final Function<Integer, String> charFn = (index) -> random.nextInt() % 2 == 0 ? "a" : "b";
      final String inputData = IntStream.range(0, 50_000)
              .mapToObj(charFn::apply)
              .collect(Collectors.joining(","));
      final Response response = client
              .target(BASE_URI)
              .path(path)
              .request()
              .post(Entity.text(inputData));
      Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
      Assert.assertEquals(inputData, response.readEntity(String.class));
   }
}
