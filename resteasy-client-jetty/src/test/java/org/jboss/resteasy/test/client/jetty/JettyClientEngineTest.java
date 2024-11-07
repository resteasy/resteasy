package org.jboss.resteasy.test.client.jetty;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.apache.http.entity.ContentType;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.engines.jetty.JettyClientEngine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JettyClientEngineTest {
    Server server = new Server(0);
    Client client;

    @AfterEach
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
            client = ClientBuilder.newClient();
        }
        return client;
    }

    @Test
    public void checkClientEngine() {
        final Client client = ClientBuilder.newClient();
        Assertions.assertInstanceOf(ResteasyClient.class, client,
                () -> String.format("Expected the client to be an instance of %s", ResteasyClient.class));
        final ResteasyClient resteasyClient = (ResteasyClient) client;
        Assertions.assertInstanceOf(JettyClientEngine.class, resteasyClient.httpEngine());
    }

    @Test
    public void testSimple() throws Exception {
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(final Request request, final org.eclipse.jetty.server.Response response,
                    final Callback callback) throws Exception {

                if (request.getHeaders().get("User-Agent").contains("Apache")) {
                    response.setStatus(503);
                } else if (!"abracadabra".equals(request.getHeaders().get("Password"))) {
                    response.setStatus(403);
                } else {
                    response.setStatus(200);
                    Content.Sink.write(response, true, "Success", callback);
                }

                return true;
            }
        });

        final Response response = client().target(baseUri()).request()
                .header("Password", "abracadabra")
                .get();

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));
    }

    @Test
    public void testSimpleResponseRx() throws Exception {
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(final Request request, final org.eclipse.jetty.server.Response response,
                    final Callback callback) throws Exception {

                if (request.getHeaders().get("User-Agent").contains("Apache")) {
                    response.setStatus(503);
                } else if (!"abracadabra".equals(request.getHeaders().get("Password"))) {
                    response.setStatus(403);
                } else {
                    response.setStatus(200);
                    response.getHeaders().put(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType());
                    Content.Sink.write(response, true, "Success", callback);
                }
                return true;
            }
        });

        final CompletionStage<Response> cs = client().target(baseUri()).request()
                .header("Password", "abracadabra").rx(CompletionStageRxInvoker.class)
                .get();

        Response response = cs.toCompletableFuture().get();
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));
    }

    @Test
    public void testSimpleStringRx() throws Exception {
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(final Request request, final org.eclipse.jetty.server.Response response,
                    final Callback callback) throws Exception {

                if (request.getHeaders().get("User-Agent").contains("Apache")) {
                    response.setStatus(503);
                } else if (!"abracadabra".equals(request.getHeaders().get("Password"))) {
                    response.setStatus(403);
                } else {
                    response.setStatus(200);
                    response.getHeaders().put(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType());
                    Content.Sink.write(response, true, "Success", callback);
                }
                return true;
            }
        });

        final CompletionStage<String> cs = client().target(baseUri()).request()
                .header("Password", "abracadabra").rx(CompletionStageRxInvoker.class)
                .get(String.class);

        String response = cs.toCompletableFuture().get();
        assertEquals("Success", response);
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

    @Test
    public void testFutureResponse() throws Exception {
        server.setHandler(new EchoHandler());
        final String valuableData = randomAlpha();
        final Future<Response> response = client().target(baseUri()).request()
                .buildPost(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE))
                .submit();

        final Response resp = response.get(10, TimeUnit.SECONDS);
        assertEquals(200, resp.getStatus());
        assertEquals(valuableData, resp.readEntity(String.class));
    }

    @Test
    public void testFutureString() throws Exception {
        server.setHandler(new EchoHandler());
        final String valuableData = randomAlpha();
        final Future<String> response = client().target(baseUri()).request()
                .buildPost(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE))
                .submit(String.class);

        final String result = response.get(10, TimeUnit.SECONDS);
        assertEquals(valuableData.length(), result.length());
        assertEquals(valuableData, result);
    }

    private String randomAlpha() {
        final StringBuilder builder = new StringBuilder();
        final Random r = new Random();
        for (int i = 0; i < 20 * 1024 * 1024; i++) {
            builder.append((char) ('a' + (char) r.nextInt('z' - 'a')));
            if (i % 100 == 0)
                builder.append('\n');
        }
        return builder.toString();
    }

    @Test
    public void testTimeout() throws Exception {
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(final Request request, final org.eclipse.jetty.server.Response response,
                    final Callback callback) throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    callback.failed(e);
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
                return true;
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
    public void testIdleTimeout() throws Exception {
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(final Request request, final org.eclipse.jetty.server.Response response,
                    final Callback callback) throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
                response.setStatus(200);
                Content.Sink.write(response, true, "Success", callback);
                return true;
            }
        });

        try {
            client().target(baseUri()).request()
                    .property(JettyClientEngine.REQUEST_TIMEOUT_MS, Duration.ofMillis(2000))
                    .property(JettyClientEngine.IDLE_TIMEOUT_MS, Duration.ofMillis(500))
                    .get();
            fail();
        } catch (ProcessingException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }

        final Response response = client().target(baseUri()).request()
                .property(JettyClientEngine.REQUEST_TIMEOUT_MS, Duration.ofMillis(2000))
                .property(JettyClientEngine.IDLE_TIMEOUT_MS, Duration.ofMillis(1500))
                .get();

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));

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

    @Test
    public void testFilterBufferReplay() throws Exception {
        final String greeting = "Success";
        final byte[] expected = (greeting).getBytes(StandardCharsets.UTF_8);
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(final Request request, final org.eclipse.jetty.server.Response response,
                    final Callback callback) throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
                response.setStatus(200);
                response.getHeaders().put(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType());
                Content.Sink.write(response, true, greeting, callback);
                return true;
            }
        });

        final byte[] content = new byte[expected.length];
        final ClientResponseFilter capturer = new ClientResponseFilter() {
            @Override
            public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
                responseContext.getEntityStream().read(content);
            }
        };

        try (
                InputStream response = client().register(capturer).target(baseUri()).request()
                        .get(InputStream.class)) {
            // ignored, we are checking filter
        }

        assertArrayEquals(expected, content);
    }

    public URI baseUri() {
        return URI.create("http://localhost:" + ((ServerConnector) server.getConnectors()[0]).getLocalPort());
    }

    static class EchoHandler extends Handler.Abstract {

        @Override
        public boolean handle(final Request request, final org.eclipse.jetty.server.Response response, final Callback callback)
                throws Exception {
            response.setStatus(200);
            long contentLength = -1;
            for (HttpField field : request.getHeaders()) {
                if (field.getHeader() != null) {
                    switch (field.getHeader()) {
                        case CONTENT_LENGTH -> {
                            response.getHeaders().add(field);
                            contentLength = field.getLongValue();
                        }
                        case CONTENT_TYPE -> response.getHeaders().add(field);
                        case TRAILER -> response.setTrailersSupplier(HttpFields.build());
                        case TRANSFER_ENCODING -> contentLength = Long.MAX_VALUE;
                    }
                }
            }
            if (contentLength > 0)
                Content.copy(request, response, org.eclipse.jetty.server.Response.newTrailersChunkProcessor(response),
                        callback);
            else
                callback.succeeded();
            return true;
        }
    }
}
