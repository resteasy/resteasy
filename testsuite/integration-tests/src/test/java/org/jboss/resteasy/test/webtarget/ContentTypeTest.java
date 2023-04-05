/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.webtarget;

import java.lang.reflect.ReflectPermission;
import java.net.URI;
import java.util.PropertyPermission;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the "content-type" HTTP header is correctly set for entities.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
public class ContentTypeTest {

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, ContentTypeTest.class.getSimpleName() + ".war")
                .addClasses(TestUtil.class, EchoHeaders.class, RestActivator.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // This can be removed if WFARQ-118 is resolved
                .addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                        // Required for Arquillian
                        new ReflectPermission("suppressAccessChecks"),
                        new PropertyPermission("arquillian.*", "read"),
                        new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
                        // Required for JUnit
                        new RuntimePermission("accessDeclaredMembers")),
                        "permissions.xml");
    }

    private static Client client;

    @ArquillianResource
    private URI uri;

    @Before
    public void createClient() {
        client = ClientBuilder.newClient();
    }

    @After
    public void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Checks that if the Content-Type HTTP header is set, that any entity set will override the request header for a
     * POST method.
     *
     * @throws Exception if a failure occurs
     */
    @Test
    public void postEntityContentType() throws Exception {
        try (
                Response response = client.target(
                        TestUtil.generateUri(uri, "headers/post"))
                        .request()
                        .header(HttpHeaders.CONTENT_TYPE, "text/invalid")
                        .post(Entity.json("{post: \"value\"}"))) {
            final JsonObject json = checkResponse(response);
            Assert.assertEquals("{post: \"value\"}", json.getString("entity"));
            final JsonObject headers = json.getJsonObject("headers");
            final JsonArray contentType = headers.getJsonArray(HttpHeaders.CONTENT_TYPE);
            Assert.assertNotNull("No content type found in: " + json, contentType);
            Assert.assertFalse("No content type found in: " + json, contentType.isEmpty());
            Assert.assertEquals(MediaType.APPLICATION_JSON, contentType.getString(0));
        }
    }

    /**
     * Checks that if the Content-Type HTTP header is set, that any entity set will override the request header for a
     * POST method.
     *
     * @throws Exception if a failure occurs
     */
    @Test
    public void putEntityContentType() throws Exception {
        try (
                Response response = client.target(
                        TestUtil.generateUri(uri, "headers/put"))
                        .request()
                        .header(HttpHeaders.CONTENT_TYPE, "text/invalid")
                        .put(Entity.json("{put: \"value\"}"))) {
            final JsonObject json = checkResponse(response);
            Assert.assertEquals("{put: \"value\"}", json.getString("entity"));
            final JsonObject headers = json.getJsonObject("headers");
            final JsonArray contentType = headers.getJsonArray(HttpHeaders.CONTENT_TYPE);
            Assert.assertNotNull("No content type found in: " + json, contentType);
            Assert.assertFalse("No content type found in: " + json, contentType.isEmpty());
            Assert.assertEquals(MediaType.APPLICATION_JSON, contentType.getString(0));
        }
    }

    private static JsonObject checkResponse(final Response response) {
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        final JsonObject json = response.readEntity(JsonObject.class);
        Assert.assertNotNull(json);
        return json;
    }

    @ApplicationPath("/headers")
    public static class RestActivator extends Application {

    }

    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequestScoped
    public static class EchoHeaders {

        @Context
        private HttpHeaders headers;

        @DELETE
        @Path("delete")
        public Response delete(final String entity) {
            final JsonObjectBuilder builder = createRequestHeaders();
            builder.add("entity", entity);
            return Response.ok(builder.build(), MediaType.APPLICATION_JSON).build();
        }

        @POST
        @Path("post")
        public Response post(final String entity) {
            final JsonObjectBuilder builder = createRequestHeaders();
            builder.add("entity", entity);
            return Response.ok(builder.build(), MediaType.APPLICATION_JSON).build();
        }

        @PUT
        @Path("put")
        public Response put(final String entity) {
            final JsonObjectBuilder builder = createRequestHeaders();
            builder.add("entity", entity);
            return Response.ok(builder.build(), MediaType.APPLICATION_JSON).build();
        }

        private JsonObjectBuilder createRequestHeaders() {
            final JsonObjectBuilder builder = Json.createObjectBuilder();
            final JsonObjectBuilder headerBuilder = Json.createObjectBuilder();

            headers.getRequestHeaders()
                    .forEach((name, value) -> headerBuilder.add(name, Json.createArrayBuilder(value)));

            builder.add("headers", headerBuilder);
            return builder;
        }
    }
}
