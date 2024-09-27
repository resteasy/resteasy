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

package org.jboss.resteasy.test.providers.jackson2.objectmapper;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.jackson.JacksonOptions;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.providers.jackson2.objectmapper.resources.Contact;
import org.jboss.resteasy.test.providers.jackson2.objectmapper.resources.User;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.0.Beta1")
abstract class AbstractObjectMapperTest {

    private final boolean expectUserAddFailure;
    private final boolean expectContactAddFailure;
    @ArquillianResource
    private URI uri;

    AbstractObjectMapperTest(final boolean expectUserAddFailure, final boolean expectContactAddFailure) {
        this.expectUserAddFailure = expectUserAddFailure;
        this.expectContactAddFailure = expectContactAddFailure;
    }

    @Test
    public void addUser() {
        try (Client client = createClient()) {
            final User user = new User();
            user.setName("RESTEasy");
            user.setEmail("resteasy@resteasy.dev");
            try (
                    Response response = client.target(UriBuilder.fromUri(uri).path("/user/add"))
                            .request()
                            .post(Entity.json(user))) {
                Assertions.assertEquals(201, response.getStatus());
                if (expectUserAddFailure) {
                    try (Response queryResponse = client.target(response.getLocation()).request().get()) {
                        final String body = queryResponse.readEntity(String.class);
                        Assertions.assertEquals(400, queryResponse.getStatus(),
                                () -> String.format("Expected 400 response from %s, but got %d with body: %s", uri,
                                        response.getStatus(), body));
                        Assertions.assertTrue(body.contains("Not able to deserialize data provided"),
                                () -> "Expected 'Not able to deserialize data provided' in body: " + body);
                    }
                } else {
                    compareUser(client, response.getLocation(), user, true);
                }
            }
        }
    }

    @Test
    public void addContact() {
        try (Client client = createClient()) {
            final Contact contact = new Contact();
            contact.setName("RESTEasy");
            contact.setEmail("resteasy@resteasy.dev");
            try (
                    Response response = client.target(UriBuilder.fromUri(uri).path("/contact/add"))
                            .request()
                            .post(Entity.json(contact))) {
                Assertions.assertEquals(201, response.getStatus());
                if (expectContactAddFailure) {
                    try (Response queryResponse = client.target(response.getLocation()).request().get()) {
                        final String body = queryResponse.readEntity(String.class);
                        Assertions.assertEquals(400, queryResponse.getStatus(),
                                () -> String.format("Expected 400 response from %s, but got %d with body: %s", uri,
                                        response.getStatus(), body));
                        Assertions.assertTrue(body.contains("Not able to deserialize data provided"),
                                () -> "Expected 'Not able to deserialize data provided' in body: " + body);
                    }
                } else {
                    compareContact(client, response.getLocation(), contact, true);
                }
            }
        }
    }

    public void modifyUser() {
        try (Client client = createClient()) {
            final Contact contact = new Contact();
            contact.setName("RESTEasy User");
            contact.setEmail("user@resteasy.dev");
            try (
                    Response response = client.target(UriBuilder.fromUri(uri).path("/user/add"))
                            .request()
                            .post(Entity.json(contact))) {
                Assertions.assertEquals(201, response.getStatus());
                try (Response queryResponse = client.target(response.getLocation()).request().get()) {
                    Assertions.assertEquals(200, queryResponse.getStatus(),
                            () -> String.format("Expected 200 response from %s, but got %d with body: %s", uri,
                                    response.getStatus(), queryResponse.readEntity(String.class)));
                    final User userToUpdate = queryResponse.readEntity(User.class);
                    userToUpdate.setPhoneNumber("+1 555.555.5555");
                    try (Response updateResponse = client.target(UriBuilder.fromUri(uri).path("/user/update"))
                            .request()
                            .put(Entity.json(userToUpdate))) {
                        Assertions.assertEquals(204, updateResponse.getStatus());
                        compareUser(client, UriBuilder.fromUri(uri).path("/user/" + userToUpdate.getId()).build(),
                                userToUpdate, false);
                    }
                }
            }
        }
    }

    public void modifyContact() throws Exception {
        try (Client client = createClient()) {
            final Contact contact = new Contact();
            contact.setName("RESTEasy User");
            contact.setEmail("user@resteasy.dev");
            try (
                    Response response = client.target(UriBuilder.fromUri(uri).path("/contact/add"))
                            .request()
                            .post(Entity.json(contact))) {
                Assertions.assertEquals(201, response.getStatus());
                try (Response queryResponse = client.target(response.getLocation()).request().get()) {
                    Assertions.assertEquals(200, queryResponse.getStatus(),
                            () -> String.format("Expected 200 response from %s, but got %d with body: %s", uri,
                                    response.getStatus(), queryResponse.readEntity(String.class)));
                    final Contact contactToUpdate = queryResponse.readEntity(Contact.class);
                    contactToUpdate.setPhoneNumber("+1 555.555.5555");
                    try (Response updateResponse = client.target(UriBuilder.fromUri(uri).path("/contact/update"))
                            .request()
                            .put(Entity.json(contactToUpdate))) {
                        Assertions.assertEquals(204, updateResponse.getStatus());
                        compareContact(client, UriBuilder.fromUri(uri).path("/contact/" + contactToUpdate.getId()).build(),
                                contactToUpdate, false);
                    }
                }
            }
        }
    }

    static WebArchive createDeployment(final Class<? extends AbstractObjectMapperTest> test,
            final boolean useDefaultObjectMapper) {
        final Map<String, String> contextParams;
        if (useDefaultObjectMapper) {
            contextParams = Map.of(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        } else {
            contextParams = Map.ofEntries(
                    Map.entry(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true"),
                    Map.entry(JacksonOptions.DISABLE_DEFAULT_OBJECT_MAPPER.name(), "true"));
        }
        return ShrinkWrap.create(WebArchive.class, test.getSimpleName() + ".war")
                .addPackages(true, User.class.getPackage())
                .addClasses(TestApplication.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml")
                .addAsWebInfResource(TestUtil.createWebXml(null, null, contextParams), "web.xml");
    }

    protected static void compareUser(final Client client, final URI uri, final User expected, final boolean added) {
        try (Response response = client.target(uri).request().get()) {
            Assertions.assertEquals(200, response.getStatus(),
                    () -> String.format("Failed to find user for %s: %s", uri, response.readEntity(String.class)));
            final User found = response.readEntity(User.class);
            // Deep compare the user read
            if (!added) {
                Assertions.assertEquals(expected, found);
                Assertions.assertEquals(expected.getCreated(), found.getCreated());
                Assertions.assertTrue(found.getModified().isPresent(),
                        () -> String.format("The modified date was not set in %s", found));
            }
            Assertions.assertEquals(expected.getName(), found.getName());
            Assertions.assertEquals(expected.getEmail(), found.getEmail());
            Assertions.assertEquals(expected.getPhoneNumber().orElse(null), found.getPhoneNumber().orElse(null));
            Assertions.assertNotNull(found.getCreated());
            Assertions.assertTrue(found.getCreated().isBefore(Instant.now()));
        }
    }

    protected static void compareContact(final Client client, final URI uri, final Contact expected, final boolean added) {
        try (Response response = client.target(uri).request().get()) {
            Assertions.assertEquals(200, response.getStatus(),
                    () -> String.format("Failed to find user for %s: %s", uri, response.readEntity(String.class)));
            final Contact found = response.readEntity(Contact.class);
            // Deep compare the user read
            if (!added) {
                Assertions.assertEquals(expected, found);
            }
            Assertions.assertEquals(expected.getName(), found.getName());
            Assertions.assertEquals(expected.getEmail(), found.getEmail());
            Assertions.assertEquals(expected.getPhoneNumber().orElse(null), found.getPhoneNumber().orElse(null));
        }
    }

    static Client createClient() {
        return ClientBuilder.newBuilder().register(new ObjectMapperContextResolver())
                .property(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true").build();
    }

    private static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

        @Override
        public ObjectMapper getContext(final Class<?> type) {
            return mapper;
        }
    }
}
