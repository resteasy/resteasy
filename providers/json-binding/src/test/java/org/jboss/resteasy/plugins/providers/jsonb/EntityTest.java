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

package org.jboss.resteasy.plugins.providers.jsonb;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jakarta.json.JsonValue;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class EntityTest {

    private static SeBootstrap.Instance INSTANCE;

    @BeforeAll
    public static void start() throws Exception {
        INSTANCE = SeBootstrap.start(TestApplication.class)
                .toCompletableFuture().get(10, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void stop() throws Exception {
        final SeBootstrap.Instance instance = INSTANCE;
        if (instance != null) {
            instance.stop()
                    .toCompletableFuture().get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testNullEntity() {
        try (Client client = ClientBuilder.newClient()) {
            try (
                    Response response = client.target(INSTANCE.configuration().baseUriBuilder().path("json"))
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.json(null))
            ) {
                Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
                Assertions.assertNull(response.readEntity(JsonValue.class));
            }
        }
    }

    @Test
    public void testNonNullEntity() {
        try (Client client = ClientBuilder.newClient()) {
            final TestEntity testEntity = new TestEntity();
            testEntity.setName("Test");
            try (
                    Response response = client.target(INSTANCE.configuration().baseUriBuilder().path("json"))
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.json(testEntity))
            ) {
                Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
                Assertions.assertEquals(testEntity, response.readEntity(TestEntity.class));
            }
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TestResource.class);
        }
    }

    @Path("/json")
    public static class TestResource {

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response entity(final TestEntity entity) {
            return Response.ok(entity, MediaType.APPLICATION_JSON_TYPE).build();
        }
    }

    public static class TestEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof TestEntity)) {
                return false;
            }
            return Objects.equals(((TestEntity) obj).name, name);
        }
    }
}
