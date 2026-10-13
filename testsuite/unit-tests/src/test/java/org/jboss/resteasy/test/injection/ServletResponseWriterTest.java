/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.injection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Content written to a {@code @Context HttpServletResponse} from a {@code void} resource method must not be replaced
 * with a 204, whether it was written through the output stream or the writer. Content discarded with
 * {@link HttpServletResponse#reset()} does not count as written.
 */
@RestBootstrap(value = ServletResponseWriterTest.Resource.class)
@Tag("cdi")
class ServletResponseWriterTest {

    @Test
    void outputStream(@RestResource @RequestPath("servlet-response/stream") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("from-stream", response.readEntity(String.class));
        }
    }

    @Test
    void writer(@RestResource @RequestPath("servlet-response/writer") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("from-writer", response.readEntity(String.class));
        }
    }

    @Test
    void writerReset(@RestResource @RequestPath("servlet-response/writer-reset") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(204, response.getStatus());
        }
    }

    @Path("servlet-response")
    public static class Resource {

        @GET
        @Path("stream")
        public void stream(@Context final HttpServletResponse response) throws IOException {
            response.getOutputStream().write("from-stream".getBytes(StandardCharsets.UTF_8));
        }

        @GET
        @Path("writer")
        public void writer(@Context final HttpServletResponse response) throws IOException {
            response.getWriter().write("from-writer");
        }

        @GET
        @Path("writer-reset")
        public void writerReset(@Context final HttpServletResponse response) throws IOException {
            response.getWriter().write("discarded");
            response.reset();
        }
    }
}
