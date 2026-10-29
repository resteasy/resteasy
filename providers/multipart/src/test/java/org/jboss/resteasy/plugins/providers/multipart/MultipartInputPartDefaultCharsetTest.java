/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2026 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.plugins.providers.multipart;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

@RestBootstrap({ MultipartInputPartDefaultCharsetTest.TestResource.class,
        MultipartInputPartDefaultCharsetTest.DefaultCharsetFilter.class })
public class MultipartInputPartDefaultCharsetTest {

    @RestResource
    @RequestPath("/test/parts")
    private WebTarget partsTarget;

    @Test
    public void defaultCharsetIsNotAppliedToOctetStream() {
        final String body = "--boundary\r\n"
                + "Content-Disposition: form-data; name=\"binary\"; filename=\"f.bin\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n"
                + "bytes\r\n"
                + "--boundary\r\n"
                + "Content-Disposition: form-data; name=\"text\"\r\n"
                + "Content-Type: text/plain\r\n\r\n"
                + "hello\r\n"
                + "--boundary\r\n"
                + "Content-Disposition: form-data; name=\"xml\"\r\n"
                + "Content-Type: application/xml\r\n\r\n"
                + "<a/>\r\n"
                + "--boundary--\r\n";

        try (Response response = partsTarget.request()
                .post(Entity.entity(body.getBytes(StandardCharsets.UTF_8), "multipart/form-data; boundary=boundary"))) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(),
                    () -> "Request failed: " + response.readEntity(String.class));
            final String result = response.readEntity(String.class);
            Assertions.assertEquals("binary=application/octet-stream\n"
                    + "text=text/plain;charset=UTF-8\n"
                    + "xml=application/xml;charset=UTF-8\n", result);
        }
    }

    @Provider
    public static class DefaultCharsetFilter implements ContainerRequestFilter {

        @Override
        public void filter(final ContainerRequestContext requestContext) {
            requestContext.setProperty(InputPart.DEFAULT_CHARSET_PROPERTY, StandardCharsets.UTF_8.name());
            requestContext.setProperty(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, MediaType.TEXT_PLAIN);
        }
    }

    @Path("/test")
    public static class TestResource {

        @POST
        @Path("/parts")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.TEXT_PLAIN)
        public String parts(final MultipartFormDataInput input) {
            final StringBuilder result = new StringBuilder();
            for (Map.Entry<String, List<InputPart>> entry : input.getFormDataMap().entrySet()) {
                for (InputPart part : entry.getValue()) {
                    result.append(entry.getKey()).append('=').append(part.getMediaType()).append('\n');
                }
            }
            return result.toString();
        }
    }
}
