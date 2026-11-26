/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that a request method which cannot have a body fails when an entity is set.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(BodyNotAllowedTest.GetResource.class)
class BodyNotAllowedTest {

    /**
     * Checks that a {@code GET} request with an entity fails without being sent, instead of being sent without the
     * entity.
     */
    @Test
    void getWithEntity(@RestResource @RequestPath("get") final WebTarget target) {
        Assertions.assertThrows(ProcessingException.class,
                () -> target.request().method("GET", Entity.text("body")));
        Assertions.assertEquals(0, GetResource.INVOCATIONS.get(), "The request was sent to the server");
    }

    @Path("/get")
    public static class GetResource {
        static final AtomicInteger INVOCATIONS = new AtomicInteger();

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            INVOCATIONS.incrementAndGet();
            return "get";
        }
    }
}
