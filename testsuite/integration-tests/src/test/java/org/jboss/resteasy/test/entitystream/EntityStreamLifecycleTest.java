/*
 * JBoss, Home of Professional Open Source.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.jboss.resteasy.test.entitystream;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.entitystream.resource.EntityStreamLifecycleFilter;
import org.jboss.resteasy.test.entitystream.resource.EntityStreamLifecycleResource;
import org.jboss.resteasy.test.entitystream.resource.EntityStreamLifecycleState;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EntityStreamLifecycleTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(EntityStreamLifecycleTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null,
                EntityStreamLifecycleFilter.class,
                EntityStreamLifecycleResource.class,
                EntityStreamLifecycleState.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EntityStreamLifecycleTest.class.getSimpleName());
    }

    @Test
    public void closesBeforeFormParamResourceInvocation() {
        try (Client client = ClientBuilder.newClient()) {
            reset(client);
            Form form = new Form().param("value", "form");
            try (Response response = client.target(generateURL("/entity-stream/form")).request()
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE))) {
                Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                Assertions.assertEquals("true:form", response.readEntity(String.class));
            }
        }
    }

    @Test
    public void closesBeforeEntityResourceInvocation() {
        try (Client client = ClientBuilder.newClient()) {
            reset(client);
            try (Response response = client.target(generateURL("/entity-stream/entity")).request()
                    .post(Entity.text("entity"))) {
                Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                Assertions.assertEquals("true:entity", response.readEntity(String.class));
            }
        }
    }

    @Test
    public void keepsRawInputStreamOpenDuringResourceInvocation() {
        try (Client client = ClientBuilder.newClient()) {
            reset(client);
            try (Response response = client.target(generateURL("/entity-stream/raw")).request()
                    .post(Entity.text("raw"))) {
                Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                Assertions.assertEquals("false:raw", response.readEntity(String.class));
            }
            Assertions.assertTrue(isClosed(client),
                    "The replacement stream was not closed after raw InputStream resource invocation");
        }
    }

    @Test
    public void restoringOriginalDoesNotCloseReplacement() {
        try (Client client = ClientBuilder.newClient()) {
            reset(client);
            try (Response response = client.target(generateURL("/entity-stream/entity")).request()
                    .header(EntityStreamLifecycleFilter.RESTORE_ORIGINAL, Boolean.TRUE.toString())
                    .post(Entity.text("entity"))) {
                Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            }
            Assertions.assertFalse(isClosed(client),
                    "The superseded replacement stream was closed after restoring the original stream");
        }
    }

    private void reset(Client client) {
        try (Response response = client.target(generateURL("/entity-stream/reset")).request().post(null)) {
            Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

    private boolean isClosed(Client client) {
        try (Response response = client.target(generateURL("/entity-stream/closed")).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            return response.readEntity(Boolean.class);
        }
    }
}
