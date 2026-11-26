/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests the read timeout against a resource which delays the response headers.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(ReadTimeoutTest.StallResource.class)
class ReadTimeoutTest {

    @AfterAll
    static void release() {
        // Let the resource finish, so the server can stop
        StallResource.RELEASE.countDown();
    }

    /**
     * Checks that a read timeout set after {@link ResteasyClientBuilder#useAsyncHttpEngine()} applies to the engine of
     * the built client.
     */
    @Test
    void readTimeoutAfterUseAsyncHttpEngine(@RestResource @RequestPath("stall/headers") final URI uri) {
        try (Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .useAsyncHttpEngine()
                .readTimeout(1, TimeUnit.SECONDS)
                .build()) {
            final ProcessingException e = Assertions.assertThrows(ProcessingException.class,
                    () -> client.target(uri).request().get(String.class),
                    "The read timeout set after useAsyncHttpEngine() was not applied");
            Assertions.assertInstanceOf(TimeoutException.class, e.getCause());
        }
    }

    @Path("/stall")
    public static class StallResource {
        static final CountDownLatch RELEASE = new CountDownLatch(1);

        @GET
        @Path("headers")
        @Produces(MediaType.TEXT_PLAIN)
        public String headers() throws InterruptedException {
            // Delay the response headers longer than the read timeout of the client
            RELEASE.await(5, TimeUnit.SECONDS);
            return "ok";
        }
    }
}
