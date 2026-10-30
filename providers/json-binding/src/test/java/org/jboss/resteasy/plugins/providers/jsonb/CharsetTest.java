/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.plugins.providers.jsonb;

import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Verifies the JSON-B provider always uses UTF-8, regardless of the requested charset.
 * See RESTEASY-2171.
 */
@RestBootstrap(CharsetTest.TestResource.class)
public class CharsetTest {

    private static final String NAME = "Graça";

    @RestResource
    @RequestPath("/json")
    private WebTarget target;

    @Test
    public void noCharset() {
        final EntityTest.TestEntity entity = new EntityTest.TestEntity();
        entity.setName(NAME);
        try (
                Response response = target
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final String json = new String(response.readEntity(byte[].class), StandardCharsets.UTF_8);
            Assertions.assertTrue(json.contains(NAME), () -> "Expected UTF-8 encoded name in " + json);
        }
    }

    @Test
    public void usAsciiCharset() {
        final EntityTest.TestEntity entity = new EntityTest.TestEntity();
        entity.setName(NAME);
        try (
                Response response = target
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE.withCharset("us-ascii")))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final String json = new String(response.readEntity(byte[].class), StandardCharsets.UTF_8);
            Assertions.assertTrue(json.contains(NAME), () -> "Expected UTF-8 encoded name in " + json);
        }
    }

    @Path("/json")
    public static class TestResource {

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response entity(final EntityTest.TestEntity entity) {
            return Response.ok(entity, MediaType.APPLICATION_JSON_TYPE).build();
        }
    }
}
