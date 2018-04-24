package org.jboss.resteasy.test.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.JettyClientEngine;
import org.junit.After;
import org.junit.Test;

public class JettyClientEngineTest {
    Server server = new Server(0);
    Client client;

    @After
    public void stop() throws Exception {
        if (client != null) {
            client.close();
        }
        server.stop();
    }

    private Client client() throws Exception {
        if (!server.isStarted()) {
            server.start();
        }
        if (client == null) {
            final HttpClient hc = new HttpClient();
            client = new ResteasyClientBuilder().httpEngine(new JettyClientEngine(hc)).build();
        }
        return client;
    }

    @Test
    public void testSimple() throws Exception {
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                baseRequest.setHandled(true);
                if (baseRequest.getHeader("User-Agent").contains("Apache")) {
                    response.setStatus(503);
                } else if (!"abracadabra".equals(baseRequest.getHeader("Password"))) {
                    response.setStatus(403);
                } else {
                    response.setStatus(200);
                    response.getWriter().println("Success");
                }
            }
        });

        final Response response = client().target(baseUri()).request()
            .header("Password", "abracadabra")
            .get();

        assertEquals(200, response.getStatus());
        assertEquals("Success\n", response.readEntity(String.class));
    }

    @Test
    public void testBigly() throws Exception {
        server.setHandler(new EchoHandler());
        final byte[] valuableData = randomAlpha().getBytes(StandardCharsets.UTF_8);
        final Response response = client().target(baseUri()).request()
                .post(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        assertEquals(200, response.getStatus());
        assertArrayEquals(valuableData, response.readEntity(byte[].class));
    }

    private String randomAlpha() {
        final StringBuilder builder = new StringBuilder();
        final Random r = new Random();
        for (int i = 0; i < 20 * 1024 * 1024; i++) {
            builder.append((char) ('a' + (char) r.nextInt('z' - 'a')));
            if (i % 100 == 0) builder.append('\n');
        }
        return builder.toString();
    }

    @Test
    public void testTimeout() throws Exception {
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
                baseRequest.setHandled(true);
            }
        });

        try {
            client().target(baseUri()).request()
                .property(JettyClientEngine.REQUEST_TIMEOUT_MS, Duration.ofMillis(500))
                .get();
            fail();
        } catch (ProcessingException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }

    @Test
    public void testDeferContent() throws Exception {
        server.setHandler(new EchoHandler());
        final byte[] valuableData = randomAlpha().getBytes(StandardCharsets.UTF_8);
        final Response response = client().target(baseUri()).request()
                .post(Entity.entity(new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new AssertionError(e);
                        }
                        output.write(valuableData);
                    }
                }, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        assertEquals(200, response.getStatus());
        assertArrayEquals(valuableData, response.readEntity(byte[].class));
    }

    public URI baseUri() {
        return URI.create("http://localhost:" + ((ServerConnector) server.getConnectors()[0]).getLocalPort());
    }

    static class EchoHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            int read;
            final byte[] data = new byte[1024];
            final ServletInputStream in = request.getInputStream();
            final ServletOutputStream out = response.getOutputStream();
            while ((read = in.read(data)) != -1) {
                out.write(data, 0, read);
            }
        }
    }
}
