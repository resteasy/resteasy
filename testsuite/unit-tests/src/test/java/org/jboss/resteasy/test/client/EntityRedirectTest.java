/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.config.Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that the request entity is sent again when a {@code 307 Temporary Redirect} is followed, and that the file a
 * large entity is written to is deleted.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(EntityRedirectTest.RedirectResource.class)
class EntityRedirectTest {
    // Larger than the memory threshold set for this test, so the entity is written to a file. It must also be smaller
    // than the 2 MB the server accepts by default.
    private static final int FILE_ENTITY_SIZE = 1024 * 1024;
    private static final String ENTITY_FILE_PATTERN = "resteasy-entity*.tmp";
    private static String previousThreshold;

    @BeforeAll
    static void lowerMemoryThreshold() {
        previousThreshold = System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "64KB");
    }

    @AfterAll
    static void restoreMemoryThreshold() {
        if (previousThreshold == null) {
            System.clearProperty(Options.ENTITY_MEMORY_THRESHOLD.name());
        } else {
            System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), previousThreshold);
        }
    }

    /**
     * Checks that an entity held in memory is sent again to the target of the redirect.
     */
    @Test
    void memoryEntityResentOnRedirect(@RestResource @RequestPath("redirect/start") final URI uri) {
        final byte[] entity = "redirected entity".getBytes(StandardCharsets.UTF_8);
        try (Client client = followRedirectsClient()) {
            try (Response response = client.target(uri).request()
                    .post(Entity.entity(entity, MediaType.APPLICATION_OCTET_STREAM_TYPE))) {
                Assertions.assertEquals(200, response.getStatus());
                Assertions.assertArrayEquals(entity, response.readEntity(byte[].class));
            }
        }
    }

    /**
     * Checks that an entity written to a file is sent again to the target of the redirect.
     */
    @Test
    void fileEntityResentOnRedirect(@RestResource @RequestPath("redirect/start") final URI uri) throws Exception {
        final EntityFileRecorder recorder = new EntityFileRecorder(entityFiles());
        final byte[] entity = new byte[FILE_ENTITY_SIZE];
        Arrays.fill(entity, (byte) 'r');
        try (Client client = followRedirectsClient().register(recorder)) {
            try (Response response = client.target(uri).request()
                    .post(Entity.entity(entity, MediaType.APPLICATION_OCTET_STREAM_TYPE))) {
                Assertions.assertEquals(200, response.getStatus());
                Assertions.assertArrayEquals(entity, response.readEntity(byte[].class));
            }
        }
        assertEntityFileDeleted(recorder);
    }

    /**
     * Checks that the file a large entity is written to is deleted when the request fails before the entity is sent.
     */
    @Test
    void fileEntityDeletedWhenConnectionFails() throws Exception {
        final EntityFileRecorder recorder = new EntityFileRecorder(entityFiles());
        final byte[] entity = new byte[FILE_ENTITY_SIZE];
        try (Client client = ClientBuilder.newClient().register(recorder)) {
            // Nothing listens on port 1, so the connection is refused
            Assertions.assertThrows(ProcessingException.class, () -> client.target("http://127.0.0.1:1/").request()
                    .post(Entity.entity(entity, MediaType.APPLICATION_OCTET_STREAM_TYPE)));
        }
        assertEntityFileDeleted(recorder);
    }

    private static void assertEntityFileDeleted(final EntityFileRecorder recorder) {
        final Set<Path> written = recorder.written;
        Assertions.assertNotNull(written, "The entity was not written");
        Assertions.assertFalse(written.isEmpty(), "The entity was not written to a file");
        for (Path file : written) {
            Assertions.assertFalse(Files.exists(file), () -> "The entity file was not deleted: " + file);
        }
    }

    private static Client followRedirectsClient() {
        return ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .setFollowRedirects(true)
                .build();
    }

    private static Set<Path> entityFiles() throws IOException {
        final Set<Path> files = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Options.ENTITY_TMP_DIR.getValue(),
                ENTITY_FILE_PATTERN)) {
            stream.forEach(files::add);
        }
        return files;
    }

    @jakarta.ws.rs.Path("/redirect")
    public static class RedirectResource {

        @POST
        @jakarta.ws.rs.Path("/start")
        @Consumes(MediaType.APPLICATION_OCTET_STREAM)
        public Response start(final byte[] entity) {
            // A relative location is resolved against the base URI of the application
            return Response.temporaryRedirect(URI.create("redirect/target")).build();
        }

        @POST
        @jakarta.ws.rs.Path("/target")
        @Consumes(MediaType.APPLICATION_OCTET_STREAM)
        @Produces(MediaType.APPLICATION_OCTET_STREAM)
        public byte[] target(final byte[] entity) {
            return entity;
        }
    }

    /**
     * Records the entity files which exist once the entity has been written, which is before the request is sent.
     */
    private static class EntityFileRecorder implements WriterInterceptor {
        private final Set<Path> before;
        private volatile Set<Path> written;

        private EntityFileRecorder(final Set<Path> before) {
            this.before = before;
        }

        @Override
        public void aroundWriteTo(final WriterInterceptorContext context) throws IOException {
            context.proceed();
            final Set<Path> files = entityFiles();
            files.removeAll(before);
            written = files;
        }
    }
}
