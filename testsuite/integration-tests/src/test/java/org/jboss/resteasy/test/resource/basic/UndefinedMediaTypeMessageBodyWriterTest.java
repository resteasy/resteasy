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

package org.jboss.resteasy.test.resource.basic;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Providers;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@ApplicationScoped
public class UndefinedMediaTypeMessageBodyWriterTest {

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(User.class,
                        Users.class,
                        Standard.class,
                        TestApplication.class,
                        TestUtil.class)
                .addAsWebInfResource(TestUtil.createBeansXml("annotated"), "beans.xml");
    }

    @ArquillianResource
    private URI uri;

    @Inject
    private Client client;

    @Inject
    private Providers providers;

    @Test
    public void checkMessageBodyWriters() throws Exception {
        final User user = new User();
        user.setName("James Perkins");
        user.setEmail("jperkins@redhat.com");
        final var mbw = providers.getMessageBodyWriter(User.class, null, null, MediaType.WILDCARD_TYPE);
        Assertions.assertNotNull(mbw);
        Assertions.assertTrue(mbw.isWriteable(User.class, null, null, MediaType.WILDCARD_TYPE));
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            mbw.writeTo(user, null, null, null, MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), out);
            // TODO (jrp) create a real message
            Assertions.assertFalse(out.toString().isEmpty());
        }
    }

    @Test
    public void checkNoAcceptResponse() throws Exception {
        try (Response response = client.target(TestUtil.generateUri(uri, "/users")).request().get()) {
            Assertions.assertEquals(200, response.getStatus(), () -> String.format("Response failed with %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
        }
    }

    @Test
    @Disabled("This needs to be re-enabled, but for now we're leaving it disabled")
    public void noAcceptString() throws Exception {
        final String entity = "Test entity";
        try (Response response = client.target(TestUtil.generateUri(uri, "/standard/string")).request()
                .post(Entity.text(entity))) {
            Assertions.assertEquals(200, response.getStatus(), () -> String.format("Response failed with %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
            Assertions.assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
            Assertions.assertEquals(entity, response.readEntity(String.class));
        }
    }

    // TODO (jrp) we should add a test for each known type listed in https://jakarta.ee/specifications/restful-ws/3.0/jakarta-restful-ws-spec-3.0#standard_entity_providers
    @Test
    @Disabled("This needs to be re-enabled, but for now we're leaving it disabled")
    public void noAcceptByte() throws Exception {
        final String entity = "Test bytes";
        try (Response response = client.target(TestUtil.generateUri(uri, "/standard/byte")).request()
                .post(Entity.text(entity))) {
            Assertions.assertEquals(200, response.getStatus(), () -> String.format("Response failed with %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
            Assertions.assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE, response.getMediaType());
            Assertions.assertEquals(entity, response.readEntity(String.class));
        }
    }

    public static class User {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }
    }

    @Path("/users")
    public static class Users {
        @GET
        public User getUser() {
            final User user = new User();
            user.setName("James Perkins");
            user.setEmail("jperkins@redhat.com");
            return user;
        }
    }

    @Path("/standard")
    public static class Standard {

        @POST
        @Path("/string")
        public String echoString(final String body) {
            return body;
        }

        @POST
        @Path("/byte")
        public byte[] echoBytes(final String body) {
            return body.getBytes(StandardCharsets.UTF_8);
        }

    }
}
