package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamingOutputTest {
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
                        try {
                            latch.countDown();
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        output.flush();
                    }
                    output.close();
                }
            };
        }

    }

    @BeforeAll
    public static void setup() throws Exception {
        NettyContainer.start().getRegistry().addPerRequestResource(Resteasy1029Netty4StreamingOutput.class);
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
    }

    @AfterAll
    public static void end() throws Exception {
        client.close();
        NettyContainer.stop();
    }

    static boolean pass = false;

    @Test
    public void testConcurrent() throws Exception {
        pass = false;
        latch = new CountDownLatch(1);
        Runnable r = new Runnable() {
            @Override
            public void run() {
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
        //      System.out.println(end);
        Assertions.assertTrue(end < 1000);
        t.join();
        Assertions.assertTrue(pass);
    }

    @Test
    public void testStreamingOutput() throws Exception {
        Response response = client.target(BASE_URI).path("test").request().get();
        Assertions.assertTrue(response.readEntity(String.class).equals("0\n" +
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
        Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }
}
