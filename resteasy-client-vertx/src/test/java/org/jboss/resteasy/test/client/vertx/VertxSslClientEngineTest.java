/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.client.vertx;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.SelfSignedCertificate;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.vertx.VertxClientHttpEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VertxSslClientEngineTest {
    private Vertx vertx;
    private HttpServer server;
    private ScheduledExecutorService executorService;
    private SelfSignedCertificate certificate;

    @Before
    public void before() throws Exception {
        vertx = Vertx.vertx();
        certificate = SelfSignedCertificate.create();
        server = vertx.createHttpServer(new HttpServerOptions()
                .setKeyCertOptions(certificate.keyCertOptions())
                .setTrustOptions(certificate.trustOptions())
                .setSsl(true)
                .setUseAlpn(true)
        );
        executorService = Executors.newSingleThreadScheduledExecutor();
        server.requestHandler(req -> {
            final HttpServerResponse response = req.response();
            if (req.getHeader("User-Agent").contains("Apache")) {
                response.setStatusCode(503).end();
            } else {
                response.end("Success " + req.version().alpnName());
            }
        });
        if (server.actualPort() == 0) {
            CompletableFuture<Void> fut = new CompletableFuture<>();
            server.listen(0, ar -> {
                if (ar.succeeded()) {
                    fut.complete(null);
                } else {
                    fut.completeExceptionally(ar.cause());
                }
            });
            fut.get(2, TimeUnit.MINUTES);
        }
    }

    @After
    public void stop() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        vertx.close(ar -> latch.countDown());
        latch.await(2, TimeUnit.MINUTES);
        executorService.shutdownNow();
        if (certificate != null) {
            certificate.delete();
        }
    }

    @Test
    public void testHTTPS() throws Exception {
        Client client = createClient(new HttpClientOptions());
        try {
            final Response resp = client.target(baseUri()).request().get();
            assertEquals(200, resp.getStatus());
            assertEquals("Success http/1.1", resp.readEntity(String.class));
        } finally {
            client.close();
        }
    }

    @Test
    public void testHTTP2() throws Exception {
        final HttpClientOptions options = new HttpClientOptions()
                .setProtocolVersion(HttpVersion.HTTP_2)
                .setUseAlpn(true);
        Client client = createClient(options);
        try {
            final Response resp = client.target(baseUri()).request().get();
            assertEquals(200, resp.getStatus());
            assertEquals("Success h2", resp.readEntity(String.class));
        } finally {
            client.close();
        }
    }

    private URI baseUri() {
        return URI.create("https://localhost:" + server.actualPort());
    }

    private Client createClient(final HttpClientOptions options) throws Exception {
        options.setKeyCertOptions(certificate.keyCertOptions())
                .setTrustOptions(certificate.trustOptions())
                .setSsl(true);
        return ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .scheduledExecutorService(executorService)
                .httpEngine(new VertxClientHttpEngine(vertx, options))
                .build();
    }
}
