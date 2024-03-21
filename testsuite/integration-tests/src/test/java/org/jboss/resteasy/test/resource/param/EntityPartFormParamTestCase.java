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

package org.jboss.resteasy.test.resource.param;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(LoggingSetupTask.class)
public class EntityPartFormParamTestCase {

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, EntityPartFormParamTestCase.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, TestResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URI uri;

    /**
     * Tests sending {@code multipart/form-data} content as a {@link EntityPart List<EntityPart>} and receiving
     * {@code multipart/form-data} content back with an additional part.
     *
     * @throws Exception if an error occurs in the test
     */
    @Test
    public void form() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final List<EntityPart> multipart = List.of(
                    EntityPart.withName("content")
                            .content("test content".getBytes(StandardCharsets.UTF_8))
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .build());
            try (
                    Response response = client.target(TestUtil.generateUri(uri, "test/form"))
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
        try (Client client = ClientBuilder.newClient()) {
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
                    Response response = client.target(TestUtil.generateUri(uri, "test/multi-injected"))
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
    }

    private static void checkEntity(final List<EntityPart> entityParts, final String name, final String expectedText)
            throws IOException {
        final EntityPart part = find(entityParts, name);
        Assertions.assertNotNull(part,
                () -> String.format("Failed to find entity part %s in: %s", name, getMessage(entityParts)));
        Assertions.assertEquals(expectedText, part.getContent(String.class));
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

    private static String toString(final InputStream in) {
        // try-with-resources fails here due to a bug in the
        //noinspection TryFinallyCanBeTryWithResources
        try {
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

    private static EntityPart find(final Collection<EntityPart> parts, final String name) {
        for (EntityPart part : parts) {
            if (name.equals(part.getName())) {
                return part;
            }
        }
        return null;
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
                            //.content(content.getContent(byte[].class))
                            .content(EntityPartFormParamTestCase.toString(in).getBytes(StandardCharsets.UTF_8))
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .build(),
                    EntityPart.withName("received-string")
                            .content(string)
                            .mediaType(MediaType.TEXT_PLAIN_TYPE)
                            .build());
            return Response.ok(new GenericEntity<>(multipart) {
            }, MediaType.MULTIPART_FORM_DATA).build();
        }
    }
}
