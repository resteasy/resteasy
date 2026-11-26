/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests the values the runtime passes to an {@link InvocationCallback} for an asynchronous invocation.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(InvocationCallbackTest.GetResource.class)
class InvocationCallbackTest {

    /**
     * Checks that a {@code 404} response passes the {@link NotFoundException} to
     * {@link InvocationCallback#failed(Throwable)}, not an exception that wraps it.
     */
    @Test
    void failedReceivesNotFoundException(@RestResource @RequestPath("missing") final WebTarget target)
            throws Exception {
        final CompletableFuture<Throwable> failure = new CompletableFuture<>();
        target.request().async().get(new InvocationCallback<String>() {
            @Override
            public void completed(final String response) {
                failure.complete(null);
            }

            @Override
            public void failed(final Throwable throwable) {
                failure.complete(throwable);
            }
        });
        Assertions.assertInstanceOf(NotFoundException.class, failure.get(10, TimeUnit.SECONDS));
    }

    /**
     * Checks that the runtime closes the {@link Response} of the {@link WebApplicationException} passed to
     * {@link InvocationCallback#failed(Throwable)} after the method returns.
     * <p>
     * The error entity is buffered, and a buffered entity can still be read after the response is closed, so the test
     * checks {@link ClientResponse#isClosed()} instead of reading the entity.
     * </p>
     */
    @Test
    void failedResponseIsClosed(@RestResource @RequestPath("get/error") final WebTarget target) {
        final AtomicReference<Throwable> received = new AtomicReference<>();
        final Future<String> future = target.request().async().get(new InvocationCallback<>() {
            @Override
            public void completed(final String response) {
            }

            @Override
            public void failed(final Throwable throwable) {
                received.set(throwable);
            }
        });
        // The future completes after the callback stage, including any close of the response
        Assertions.assertThrows(ExecutionException.class, () -> future.get(10, TimeUnit.SECONDS));
        final WebApplicationException error = Assertions.assertInstanceOf(WebApplicationException.class, received.get());
        final ClientResponse response = Assertions.assertInstanceOf(ClientResponse.class, error.getResponse());
        Assertions.assertTrue(response.isClosed(),
                "The response passed to InvocationCallback.failed() was not closed");
    }

    /**
     * Checks that the runtime closes the {@link Response} passed to {@link InvocationCallback#completed(Object)}
     * after the method returns.
     */
    @Test
    void completedResponseIsClosed(@RestResource @RequestPath("get") final WebTarget target) throws Exception {
        final AtomicReference<Response> received = new AtomicReference<>();
        final Future<Response> future = target.request().async().get(new InvocationCallback<>() {
            @Override
            public void completed(final Response response) {
                received.set(response);
            }

            @Override
            public void failed(final Throwable throwable) {
            }
        });
        // The future completes after the callback stage, including any close of the response
        future.get(10, TimeUnit.SECONDS);
        final Response response = received.get();
        Assertions.assertNotNull(response, "InvocationCallback.completed() was not invoked");
        Assertions.assertThrows(IllegalStateException.class, () -> response.readEntity(String.class),
                "The response passed to InvocationCallback.completed() was not closed");
    }

    /**
     * Checks that the {@link Response} returned by the {@link Future} has the status seen in
     * {@link InvocationCallback#completed(Object)}.
     */
    @Test
    void futureResponseStatusMatchesCompleted(@RestResource @RequestPath("get") final WebTarget target)
            throws Exception {
        final AtomicInteger status = new AtomicInteger(-1);
        final Future<Response> future = target.request().async().get(new InvocationCallback<>() {
            @Override
            public void completed(final Response response) {
                status.set(response.getStatus());
            }

            @Override
            public void failed(final Throwable throwable) {
            }
        });
        final Response response = future.get(10, TimeUnit.SECONDS);
        Assertions.assertEquals(status.get(), response.getStatus());
    }

    @Path("/get")
    public static class GetResource {

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "get";
        }

        @GET
        @Path("error")
        @Produces(MediaType.TEXT_PLAIN)
        public Response error() {
            return Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        }
    }
}
