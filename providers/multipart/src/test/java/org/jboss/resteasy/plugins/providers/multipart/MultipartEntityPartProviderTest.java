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

package org.jboss.resteasy.plugins.providers.multipart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(MultipartEntityPartProviderTest.TestApplication.class)
public class MultipartEntityPartProviderTest {
    @Inject
    @RequestPath("/test/form")
    private WebTarget testFormTarget;

    @Inject
    @RequestPath("test/multi-injected")
    private WebTarget testMultiInjectedTarget;

    @Test
    public void checkGetContent() throws Exception {
        final EntityPart part = EntityPart.withName("content")
                .content("test content".getBytes(StandardCharsets.UTF_8))
                .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .build();
        Assertions.assertEquals("test content", toString(part.getContent()));
        Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(String.class));
        Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(new GenericType<>(String.class)));
        // Should be able to invoke this twice
        Assertions.assertEquals("test content", toString(part.getContent()));
    }

    @Test
    public void checkGetContentClass() throws Exception {
        final EntityPart part = EntityPart.withName("content")
                .content("test content".getBytes(StandardCharsets.UTF_8))
                .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .build();
        Assertions.assertEquals("test content", part.getContent(String.class));
        Assertions.assertEquals("test content", toString(part.getContent()));
        Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(String.class));
        Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(new GenericType<>(String.class)));
    }

    @Test
    public void checkGetContentGenericType() throws Exception {
        final EntityPart part = EntityPart.withName("content")
                .content("test content".getBytes(StandardCharsets.UTF_8))
                .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .build();
        Assertions.assertEquals("test content", part.getContent(new GenericType<>(String.class)));
        Assertions.assertEquals("test content", toString(part.getContent()));
        Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(new GenericType<>(String.class)));
        Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(String.class));
    }

    @Test
    public void checkClosed() throws Exception {
        final CloseTrackingInputStream inputStream = new CloseTrackingInputStream("test content");
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("content")
                        .content(inputStream)
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testFormTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertTrue(inputStream.isClose(), "Expected the input stream to be closed on a send.");
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            EntityPart part = find(entityParts, "received-content");
            Assertions.assertEquals("test content", part.getContent(String.class));
        }
    }

    @Test
    public void checkGetContentResponse() throws Exception {
        final CloseTrackingInputStream inputStream = new CloseTrackingInputStream("test content");
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("content")
                        .content(inputStream)
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testFormTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertTrue(inputStream.isClose(), "Expected the input stream to be closed on a send.");
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            EntityPart part = find(entityParts, "received-content");

            Assertions.assertEquals("test content", part.getContent(String.class));
            Assertions.assertEquals("test content", toString(part.getContent()));
            Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(String.class));
            Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(new GenericType<>(String.class)));
            // Should be able to invoke this twice
            Assertions.assertEquals("test content", toString(part.getContent()));
        }
    }

    @Test
    public void checkGetContentClassResponse() throws Exception {
        final CloseTrackingInputStream inputStream = new CloseTrackingInputStream("test content");
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("content")
                        .content(inputStream)
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testFormTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertTrue(inputStream.isClose(), "Expected the input stream to be closed on a send.");
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            EntityPart part = find(entityParts, "received-content");

            Assertions.assertEquals("test content", part.getContent(String.class));
            Assertions.assertEquals("test content", toString(part.getContent()));
            Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(String.class));
            Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(new GenericType<>(String.class)));
        }
    }

    @Test
    public void checkGetContentGenericTypeResponse() throws Exception {
        final CloseTrackingInputStream inputStream = new CloseTrackingInputStream("test content");
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("content")
                        .content(inputStream)
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testFormTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertTrue(inputStream.isClose(), "Expected the input stream to be closed on a send.");
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            EntityPart part = find(entityParts, "received-content");

            Assertions.assertEquals("test content", part.getContent(new GenericType<>(String.class)));
            Assertions.assertEquals("test content", toString(part.getContent()));
            Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(new GenericType<>(String.class)));
            Assertions.assertThrows(IllegalStateException.class, () -> part.getContent(String.class));
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>} and receiving
     * {@code multipart/form-data} content back with an additional part.
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void form() throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("content")
                        .content("test content".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testFormTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            EntityPart part = find(entityParts, "received-content");
            Assertions.assertNotNull(part, () -> getMessage(entityParts));
            Assertions.assertEquals("test content", part.getContent(String.class));

            part = find(entityParts, "added-content");
            Assertions.assertNotNull(part, () -> getMessage(entityParts));
            Assertions.assertEquals("test added content", part.getContent(String.class));
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Three parts are sent
     * and injected as {@link FormParam @FormParam} method parameters. Each part send is different and injected as a
     * specific type.
     * <p>
     * The result from the REST endpoint is {@code multipart/form-data} content with a new name and the content for the
     * injected field.
     * </p>
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void multiInjection() throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("entity-part")
                        .content("test entity part")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("string-part")
                        .content("test string")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .content("test input stream".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testMultiInjectedTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 3) {
                final String msg = "Expected 3 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            checkEntity(entityParts, "received-entity-part", "test entity part");
            checkEntity(entityParts, "received-string", "test string");
            checkEntity(entityParts, "received-input-stream", "test input stream");
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Two parts are sent
     * and injected as {@link FormParam @FormParam} method parameters. Each part send is different and injected as a
     * specific type.
     * <p>
     * The result from the REST endpoint is {@code multipart/form-data} content with a new name and the content for the
     * injected field.
     * </p>
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void multiInjectionNoEntityPart(@RequestPath("test/multi-injected-no-entity-part") final WebTarget target)
            throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("string-part")
                        .content("test string")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .content("test input stream".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = target
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            checkEntity(entityParts, "received-string", "test string");
            checkEntity(entityParts, "received-input-stream", "test input stream");
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Three parts are sent
     * and injected as {@link FormParam @FormParam} method parameters. Each part send is different and injected as a
     * specific type.
     * <p>
     * The result from the REST endpoint is {@code multipart/form-data} content with a new name and the content for the
     * injected field.
     * </p>
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void multiAllFilesInjection() throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("entity-part")
                        .fileName("file1.txt")
                        .content("test entity part file1.txt")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("string-part")
                        .fileName("file2.txt")
                        .content("test string file2.txt")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .fileName("file3.txt")
                        .content("test input stream file3.txt".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = testMultiInjectedTarget
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 3) {
                final String msg = "Expected 3 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }
            checkEntity(entityParts, "received-entity-part", "test entity part file1.txt");
            checkEntity(entityParts, "received-string", "test string file2.txt");
            checkEntity(entityParts, "received-input-stream", "test input stream file3.txt");
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Three parts are sent
     * and processed by the resource returning all the headers from the request.
     * <p>
     * The result from the REST endpoint is a list of {@link EntityPart}'s which content the content from the entity
     * sent, plus any header beginning with "test-".
     * </p>
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void echo(@RequestPath("/test/echo") final WebTarget target) throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("entity-part")
                        .content("test entity part")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .header("test-entity-1", "part1")
                        .header("test-content-type", MediaType.TEXT_PLAIN)
                        .build(),
                EntityPart.withName("string-part")
                        .content("test string")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .header("test-entity-2", "part2")
                        .header("test-content-type", MediaType.TEXT_PLAIN)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .content("test input stream".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .header("test-entity-3", "part3")
                        .header("test-content-type", MediaType.APPLICATION_OCTET_STREAM)
                        .build());
        try (
                Response response = target
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 3) {
                final String msg = "Expected 3 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }

            EntityPart part = find(entityParts, "received-entity-part");
            checkEntity(part, "test entity part");
            MultivaluedMap<String, String> headers = part.getHeaders();
            checkHeader(headers, "test-entity-1", "part1");
            checkHeader(headers, "test-content-type", MediaType.TEXT_PLAIN);

            part = find(entityParts, "received-string-part");
            checkEntity(part, "test string");
            headers = part.getHeaders();
            checkHeader(headers, "test-entity-2", "part2");
            checkHeader(headers, "test-content-type", MediaType.TEXT_PLAIN);

            part = find(entityParts, "received-input-stream-part");
            checkEntity(part, "test input stream");
            headers = part.getHeaders();
            checkHeader(headers, "test-entity-3", "part3");
            checkHeader(headers, "test-content-type", MediaType.APPLICATION_OCTET_STREAM);
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Two parts are sent
     * with no content and processed by the resource returning all the headers from the request.
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void echoEmptyContent(@RequestPath("/test/echo") final WebTarget target) throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("entity-part")
                        .content("test.txt", new ByteArrayInputStream(new byte[0]))
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .content("test.csv", new ByteArrayInputStream(new byte[0]))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .build());
        try (
                Response response = target
                        .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final List<EntityPart> entityParts = response.readEntity(new GenericType<>() {
            });
            if (entityParts.size() != 2) {
                final String msg = "Expected 2 entries got " +
                        entityParts.size() +
                        '.' +
                        System.lineSeparator() +
                        getMessage(entityParts);
                Assertions.fail(msg);
            }

            EntityPart part = find(entityParts, "received-entity-part");
            checkEntity(part, "");

            // Empty input stream
            part = find(entityParts, "received-input-stream-part");
            checkEntity(part, "");
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Three parts are sent
     * and processed by the resource returning all the headers from the request.
     * <p>
     * The result from the REST endpoint is a JSON object containing the headers for each entry.
     * <br>
     * Example:
     * <code>
     *     <pre>
     * {
     *     "entity-part": {
     *         "Content-Disposition": [
     *             "form-data; name=\"entity-part\""
     *         ],
     *         "Content-Type": [
     *             "text/plain"
     *         ],
     *         "test-content-type": [
     *             "text/plain"
     *         ],
     *         "test-entity-1": [
     *             "part1"
     *         ]
     *     }
     * }
     *     </pre>
     * </code>
     * </p>
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void echoHeaders(@RequestPath("/test/echo-headers") final WebTarget target) throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("entity-part")
                        .content("test entity part")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .header("test-entity-1", "part1")
                        .header("test-content-type", MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM)
                        .build(),
                EntityPart.withName("string-part")
                        .content("test string")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .header("test-entity-2", "part2")
                        .header("test-content-type", MediaType.TEXT_PLAIN)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .content("test input stream".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .header("test-entity-3", "part3")
                        .header("test-content-type", MediaType.APPLICATION_OCTET_STREAM)
                        .build());
        try (
                Response response = target
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(new GenericEntity<>(multipart) {
                        }, MediaType.MULTIPART_FORM_DATA))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());

            final JsonObject json = response.readEntity(JsonObject.class);
            JsonObject part = json.getJsonObject("entity-part");
            checkHeader(part, "test-entity-1", "part1");
            checkHeader(part, "test-content-type", MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM);

            part = json.getJsonObject("string-part");
            checkHeader(part, "test-entity-2", "part2");
            checkHeader(part, "test-content-type", MediaType.TEXT_PLAIN);

            part = json.getJsonObject("input-stream-part");
            checkHeader(part, "test-entity-3", "part3");
            checkHeader(part, "test-content-type", MediaType.APPLICATION_OCTET_STREAM);
        }
    }

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>}. Three parts are sent
     * and processed by the resource returning all the headers from the request. This is done through an client
     * proxy asynchronously.
     * <p>
     * The result from the REST endpoint is a JSON object containing the headers for each entry.
     * <br>
     * Example:
     * <code>
     *     <pre>
     * {
     *     "entity-part": {
     *         "Content-Disposition": [
     *             "form-data; name=\"entity-part\""
     *         ],
     *         "Content-Type": [
     *             "text/plain"
     *         ],
     *         "test-content-type": [
     *             "text/plain"
     *         ],
     *         "test-entity-1": [
     *             "part1"
     *         ]
     *     }
     * }
     *     </pre>
     * </code>
     * </p>
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void echoHeadersAsync(@RequestPath("test") final ResteasyWebTarget target) throws Exception {
        final List<EntityPart> multipart = List.of(
                EntityPart.withName("entity-part")
                        .content("test entity part")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .header("test-entity-1", "part1")
                        .header("test-content-type", MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM)
                        .build(),
                EntityPart.withName("string-part")
                        .content("test string")
                        .mediaType(MediaType.TEXT_PLAIN_TYPE)
                        .header("test-entity-2", "part2")
                        .header("test-content-type", MediaType.TEXT_PLAIN)
                        .build(),
                EntityPart.withName("input-stream-part")
                        .content("test input stream".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                        .header("test-entity-3", "part3")
                        .header("test-content-type", MediaType.APPLICATION_OCTET_STREAM)
                        .build());
        // Create the proxy client
        final AsyncClient client = target.proxy(AsyncClient.class);
        try (Response response = client.asyncHeaders(multipart).toCompletableFuture().get(5L, TimeUnit.SECONDS)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());

            final JsonObject json = response.readEntity(JsonObject.class);
            JsonObject part = json.getJsonObject("entity-part");
            checkHeader(part, "test-entity-1", "part1");
            checkHeader(part, "test-content-type", MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM);

            part = json.getJsonObject("string-part");
            checkHeader(part, "test-entity-2", "part2");
            checkHeader(part, "test-content-type", MediaType.TEXT_PLAIN);

            part = json.getJsonObject("input-stream-part");
            checkHeader(part, "test-entity-3", "part3");
            checkHeader(part, "test-content-type", MediaType.APPLICATION_OCTET_STREAM);
        }
    }

    private static void checkEntity(final List<EntityPart> entityParts, final String name, final String expectedText)
            throws IOException {
        final EntityPart part = find(entityParts, name);
        checkEntity(part, expectedText);
    }

    private static void checkEntity(final EntityPart part, final String expectedText)
            throws IOException {
        Assertions.assertEquals(expectedText, part.getContent(String.class));
    }

    private static void checkHeader(final JsonObject json, final String name, final String... expectedValues) {
        final JsonArray array = json.getJsonArray(name);
        Assertions.assertNotNull(array, () -> String.format("Failed to find array %s in %s", name, json));
        final List<String> found = array.stream()
                .filter(v -> v.getValueType() == JsonValue.ValueType.STRING)
                .map(v -> ((JsonString) v).getString())
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(List.of(expectedValues), found);
    }

    private static void checkHeader(final MultivaluedMap<String, String> headers, final String name,
            final String expectedValue) {
        Assertions.assertEquals(expectedValue, headers.getFirst(name),
                () -> String.format("Missing header name \"%s\" in %s", name, formatHeaders(new StringBuilder(), headers)));
    }

    private static String getMessage(final List<EntityPart> parts) {
        final StringBuilder msg = new StringBuilder();
        final Iterator<EntityPart> iter = parts.iterator();
        while (iter.hasNext()) {
            final EntityPart part = iter.next();
            msg.append('[')
                    .append(part.getName())
                    .append("={")
                    .append("headers=")
                    .append(part.getHeaders())
                    .append(", mediaType=")
                    .append(part.getMediaType())
                    .append(", body=")
                    .append(toString(part.getContent()))
                    .append('}');
            if (iter.hasNext()) {
                msg.append("], ");
            } else {
                msg.append(']');
            }
        }
        return msg.toString();
    }

    private static StringBuilder formatHeaders(final StringBuilder builder, final MultivaluedMap<String, String> headers) {
        for (var entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }
        return builder;
    }

    private static String toString(final InputStream in) {
        try {
            // try-with-resources fails here due to a bug in the
            //noinspection TryFinallyCanBeTryWithResources
            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[32];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                return out.toString(StandardCharsets.UTF_8);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static EntityPart find(final List<EntityPart> parts, final String name) {
        for (EntityPart part : parts) {
            if (name.equals(part.getName())) {
                return part;
            }
        }
        throw new RuntimeException("Could not find entity part named " + name + " - " + getMessage(parts));
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TestResource.class);
        }
    }

    @Path("/test")
    public static class TestResource {

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.MULTIPART_FORM_DATA)
        @Path("/form")
        public Response form(final List<EntityPart> parts) throws IOException {
            final List<EntityPart> multipart = List.of(
                    EntityPart.withName("received-content")
                            .content(parts.get(0).getContent(byte[].class))
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .build(),
                    EntityPart.withName("added-content")
                            .content("test added content".getBytes(StandardCharsets.UTF_8))
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .build());
            return Response.ok(new GenericEntity<>(multipart) {
            }, MediaType.MULTIPART_FORM_DATA).build();
        }

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.MULTIPART_FORM_DATA)
        @Path("/multi-injected")
        public Response multipleInjectable(@FormParam("string-part") final String string,
                @FormParam("entity-part") final EntityPart entityPart,
                @FormParam("input-stream-part") final InputStream in) throws IOException {
            final List<EntityPart> multipart = List.of(
                    EntityPart.withName("received-entity-part")
                            .content(entityPart.getContent(String.class))
                            .mediaType(entityPart.getMediaType())
                            .fileName(entityPart.getFileName().orElse(null))
                            .build(),
                    EntityPart.withName("received-input-stream")
                            .content(MultipartEntityPartProviderTest.toString(in).getBytes(StandardCharsets.UTF_8))
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .build(),
                    EntityPart.withName("received-string")
                            .content(string)
                            .mediaType(MediaType.TEXT_PLAIN_TYPE)
                            .build());
            return Response.ok(new GenericEntity<>(multipart) {
            }, MediaType.MULTIPART_FORM_DATA).build();
        }

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.MULTIPART_FORM_DATA)
        @Path("/multi-injected-no-entity-part")
        public Response multipleInjectableNoEntityPart(@FormParam("string-part") final String string,
                @FormParam("input-stream-part") final InputStream in) throws IOException {
            final List<EntityPart> multipart = List.of(
                    EntityPart.withName("received-input-stream")
                            .content(MultipartEntityPartProviderTest.toString(in).getBytes(StandardCharsets.UTF_8))
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .build(),
                    EntityPart.withName("received-string")
                            .content(string)
                            .mediaType(MediaType.TEXT_PLAIN_TYPE)
                            .build());
            return Response.ok(new GenericEntity<>(multipart) {
            }, MediaType.MULTIPART_FORM_DATA).build();
        }

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.MULTIPART_FORM_DATA)
        @Path("/echo")
        public List<EntityPart> echo(final List<EntityPart> entityParts) throws IOException {
            final List<EntityPart> resultParts = new ArrayList<>(entityParts.size());
            for (EntityPart entityPart : entityParts) {
                final EntityPart part = EntityPart.withName("received-" + entityPart.getName())
                        .content(entityPart.getContent())
                        .mediaType(entityPart.getMediaType())
                        .fileName(entityPart.getFileName().orElse(null))
                        .headers(filterHeaders(entityPart.getHeaders()))
                        .build();
                resultParts.add(part);
            }
            return resultParts;
        }

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/echo-headers")
        public Response echoHeaders(final List<EntityPart> entityParts) {
            final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            for (EntityPart entityPart : entityParts) {
                final JsonObjectBuilder headerObjectBuilder = Json.createObjectBuilder();
                entityPart.getHeaders().forEach((name, values) -> {
                    final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    for (String value : values) {
                        arrayBuilder.add(value);
                    }
                    headerObjectBuilder.add(name, arrayBuilder);
                });
                objectBuilder.add(entityPart.getName(), headerObjectBuilder);
            }
            return Response.ok(objectBuilder.build()).build();
        }

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/async")
        public CompletableFuture<Response> asyncHeaders(final List<EntityPart> entityParts) {
            return CompletableFuture.supplyAsync(() -> {
                final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                for (EntityPart entityPart : entityParts) {
                    final JsonObjectBuilder headerObjectBuilder = Json.createObjectBuilder();
                    entityPart.getHeaders().forEach((name, values) -> {
                        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                        for (String value : values) {
                            arrayBuilder.add(value);
                        }
                        headerObjectBuilder.add(name, arrayBuilder);
                    });
                    objectBuilder.add(entityPart.getName(), headerObjectBuilder);
                }
                return Response.ok(objectBuilder.build()).build();
            });
        }

        private static MultivaluedMap<String, String> filterHeaders(final MultivaluedMap<String, String> headers) {
            final MultivaluedMap<String, String> filtered = new MultivaluedHashMap<>();
            for (var entry : headers.entrySet()) {
                if (entry.getKey().startsWith("test-")) {
                    filtered.put(entry.getKey(), entry.getValue());
                }
            }
            return filtered;
        }
    }

    interface AsyncClient {

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/async")
        CompletionStage<Response> asyncHeaders(List<EntityPart> entityParts);
    }

    private static class CloseTrackingInputStream extends ByteArrayInputStream {
        private final AtomicBoolean closed = new AtomicBoolean(false);

        CloseTrackingInputStream(final String text) {
            super(text.getBytes(StandardCharsets.UTF_8));
        }

        boolean isClose() {
            return closed.get();
        }

        @Override
        public void close() throws IOException {
            closed.set(true);
            super.close();
        }
    }
}
