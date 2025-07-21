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

import java.net.URI;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests the "content-type" HTTP header is correctly set for entities.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
public class ContentTypeTest {

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, ContentTypeTest.class.getSimpleName() + ".war")
                .addClasses(TestUtil.class, EchoHeaders.class, RestActivator.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private Client client;

    @ArquillianResource
    private URI uri;

    /**
     * Checks that if the Content-Type HTTP header is set, that any entity set will override the request header for a
     * POST method.
     *
     * @throws Exception if a failure occurs
     */
    @Test
    public void postEntityContentType() throws Exception {
        try (Response response = client.target(
                TestUtil.generateUri(uri, "headers/post"))
                .request()
                .header(HttpHeaders.CONTENT_TYPE, "text/invalid")
                .post(Entity.json("{post: \"value\"}"))) {
            final JsonObject json = checkResponse(response);
            Assertions.assertEquals("{post: \"value\"}", json.getString("entity"));
            final JsonObject headers = json.getJsonObject("headers");
            final JsonArray contentType = headers.getJsonArray(HttpHeaders.CONTENT_TYPE);
            Assertions.assertNotNull(contentType, "No content type found in: " + json);
            Assertions.assertFalse(contentType.isEmpty(), "No content type found in: " + json);
            Assertions.assertEquals(MediaType.APPLICATION_JSON, contentType.getString(0));
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
        try (Response response = client.target(
                TestUtil.generateUri(uri, "headers/put"))
                .request()
                .header(HttpHeaders.CONTENT_TYPE, "text/invalid")
                .put(Entity.json("{put: \"value\"}"))) {
            final JsonObject json = checkResponse(response);
            Assertions.assertEquals("{put: \"value\"}", json.getString("entity"));
            final JsonObject headers = json.getJsonObject("headers");
            final JsonArray contentType = headers.getJsonArray(HttpHeaders.CONTENT_TYPE);
            Assertions.assertNotNull(contentType, "No content type found in: " + json);
            Assertions.assertFalse(contentType.isEmpty(), "No content type found in: " + json);
            Assertions.assertEquals(MediaType.APPLICATION_JSON, contentType.getString(0));
        }
    }

    private static JsonObject checkResponse(final Response response) {
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        final JsonObject json = response.readEntity(JsonObject.class);
        Assertions.assertNotNull(json);
        return json;
    }

    @ApplicationPath("/headers")
    public static class RestActivator extends Application {

    }

    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequestScoped
    public static class EchoHeaders {

        @Inject
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
