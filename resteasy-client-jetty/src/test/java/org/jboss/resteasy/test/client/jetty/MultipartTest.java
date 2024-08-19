/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.client.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.eclipse.jetty.client.HttpClient;
import org.jboss.resteasy.client.jaxrs.engines.jetty.JettyClientEngine;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestClientConfig;
import dev.resteasy.junit.extension.api.RestClientBuilderProvider;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(MultipartTest.TestApplication.class)
public class MultipartTest {

    public static class JettyClientBuilder implements RestClientBuilderProvider {
        @Override
        public ClientBuilder getClientBuilder() {
            return ClientBuilder.newBuilder().register(new JettyClientEngine(new HttpClient()));
        }
    }

    @Inject
    @RestClientConfig(JettyClientBuilder.class)
    private Client client;

    @Test
    public void checkEntityPart(final UriBuilder uriBuilder) throws IOException {
        // Create the entity parts for the request
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("name")
                        .content("RESTEasy")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("entity")
                        .content("entity-part")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("data")
                        .content("test-data".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = client.target(uriBuilder.path("/echo/entity-parts"))
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA_TYPE))) {
            assertResponse(response);
        }
    }

    @Test
    public void checkMultipartFormData(final UriBuilder uriBuilder) {
        final var multipart = new MultipartFormDataOutput();
        multipart.addFormData("name", "RESTEasy", MediaType.TEXT_PLAIN_TYPE);
        multipart.addFormData("entity", "plain text", MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multipart.addFormData("data", "test", MediaType.APPLICATION_OCTET_STREAM_TYPE, "data.txt");
        final var multipartEntity = Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA);
        final Invocation invocation = client.target(uriBuilder.path("/echo/multipart-form-data"))
                .request()
                .buildPost(multipartEntity);
        try (Response response = invocation.invoke()) {
            assertResponse(response);
        }
    }

    private static void assertResponse(final Response response) {
        Assertions.assertEquals(200, response.getStatus(),
                () -> String.format("Expected status of 200 got %d. Response: %s", response.getStatus(),
                        response.readEntity(String.class)));
        final var json = response.readEntity(JsonObject.class);
        final var headers = json.getJsonObject("headers");
        Assertions.assertNotNull(headers, () -> String.format("Failed to find headers in %s", json));
        final var contentType = headers.getJsonArray("content-type");
        Assertions.assertNotNull(contentType, () -> String.format("Failed to find content-type in %s", headers));
        Assertions.assertTrue(contentType.toString().contains("boundary"),
                () -> String.format("Failed to find boundary in %s", contentType));
    }

    @ApplicationPath("/")
    @ApplicationScoped
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TestResource.class);
        }
    }

    @Path("/echo")
    @RequestScoped
    public static class TestResource {

        @Inject
        private HttpHeaders headers;

        @POST
        @Path("entity-parts")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        public Response upload(@FormParam("name") final String name, @FormParam("data") final InputStream data,
                @FormParam("entity") final EntityPart entityPart) {
            final JsonObjectBuilder builder = Json.createObjectBuilder();
            addHeaders(builder);
            builder.add("name", name);

            // Read the data into a string
            try (data) {
                builder.add("data", new String(data.readAllBytes()));
            } catch (IOException e) {
                throw new ServerErrorException("Failed to read data " + data, Response.Status.BAD_REQUEST);
            }
            try {
                builder.add(entityPart.getName(), entityPart.getContent(String.class));
            } catch (IOException e) {
                throw new ServerErrorException("Failed to read entity " + entityPart, Response.Status.BAD_REQUEST);
            }
            return Response.ok(builder.build()).build();
        }

        @POST
        @Path("/multipart-form-data")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        public Response echo(final MultipartFormDataInput input) {
            final JsonObjectBuilder builder = Json.createObjectBuilder();
            addHeaders(builder);
            builder.add("name", readInputPart(input, "name"));
            builder.add("data", readInputPart(input, "data"));
            builder.add("entity", readInputPart(input, "entity"));
            return Response.ok(builder.build()).build();
        }

        private String readInputPart(final MultipartFormDataInput input, final String name) {
            try {
                return input.getFormDataPart(name, String.class, null);
            } catch (IOException e) {
                throw new ServerErrorException("Failed to read entity input part " + name, Response.Status.BAD_REQUEST);
            }
        }

        private void addHeaders(final JsonObjectBuilder builder) {
            final JsonObjectBuilder headerBuilder = Json.createObjectBuilder();
            headers.getRequestHeaders()
                    .forEach((headerName, values) -> headerBuilder.add(headerName.toLowerCase(Locale.ROOT),
                            Json.createArrayBuilder(values)));
            builder.add("headers", headerBuilder.build());
        }
    }
}
