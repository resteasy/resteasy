/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.injection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that resource method parameters of the {@link AsyncResponse} and {@link SseEventSink} types are injected
 * without the {@code @Suspended} or {@code @Context} annotation.
 *
 * @since 8.0
 */
@RestBootstrap(value = CdiUnannotatedParameterTest.UnannotatedResource.class)
@Tag("cdi")
class CdiUnannotatedParameterTest {

    @Test
    void asyncResponse(@RestResource @RequestPath("unannotated/async") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("async", response.readEntity(String.class));
        }
    }

    @Test
    void sseEventSink(@RestResource @RequestPath("unannotated/sse") final WebTarget target) {
        try (Response response = target.request(MediaType.SERVER_SENT_EVENTS).get()) {
            Assertions.assertEquals(200, response.getStatus());
            final String events = response.readEntity(String.class);
            Assertions.assertTrue(events.contains("data:test") || events.contains("data: test"),
                    () -> "Unexpected events: " + events);
        }
    }

    @Path("unannotated")
    public static class UnannotatedResource {

        @GET
        @Path("async")
        @Produces(MediaType.TEXT_PLAIN)
        public void async(final AsyncResponse asyncResponse) {
            asyncResponse.resume("async");
        }

        @GET
        @Path("sse")
        @Produces(MediaType.SERVER_SENT_EVENTS)
        public CompletionStage<?> sse(final SseEventSink eventSink, final Sse sse) {
            return eventSink.send(sse.newEvent("test"))
                    .whenComplete((BiConsumer<Object, Throwable>) (unused, throwable) -> {
                        try {
                            eventSink.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }
}
