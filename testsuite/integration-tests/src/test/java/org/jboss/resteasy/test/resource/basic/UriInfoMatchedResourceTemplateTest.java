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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Supplier;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * An abstract test for testing the {@link UriInfo#getMatchedResourceTemplate()}.
 * <p>
 * This provides interfaces and base types for the resource and sub-resource. The default implementations simply return
 * a JSON response with the {@link UriInfo#getPath()} and {@link UriInfo#getMatchedResourceTemplate()}. Other fields
 * may be added depending on the resource.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.0.Alpha1")
public abstract class UriInfoMatchedResourceTemplateTest {

    /**
     * Creates a default deployment with the deployment name being the name of the test class. This adds all the
     * required classes and an empty {@code beans.xml} file.
     *
     * @param testClass the test class used for the deployment name
     *
     * @return a WAR for the deployment
     */
    static WebArchive createDeployment(final Class<? extends UriInfoMatchedResourceTemplateTest> testClass) {
        return ShrinkWrap.create(WebArchive.class, testClass.getSimpleName() + ".war")
                .addClasses(
                        UriInfoResource.class,
                        AbstractUriInfoResource.class,
                        SubResource.class,
                        DefaultSubResource.class,
                        UriInfoMatchedResourceTemplateTest.class, testClass)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URI uri;

    /**
     * Tests that an empty path returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void empty() throws Exception {
        final var json = get("");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath()), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, applicationPath(), resourcePath()),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Tests that a simple path returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/simple}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void simple() throws Exception {
        final var json = get("simple");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "simple"), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, applicationPath(), resourcePath(), "simple"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Tests that a single complex path returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/complex//{id:[a-m][n-z][0-9]+}}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void complex() throws Exception {
        final var json = get("/complex/lr52");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals("lr52", json.getString("id"), errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "/complex/lr52"), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, applicationPath(), resourcePath(), "/complex/{id:[a-m][n-z][0-9]+}"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Tests that a two complex paths return the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/two-complex/{id:[0-9]+}/{name:[a-zA-Z]+}}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void twoComplex() throws Exception {
        final var json = get("two-complex/21/abcXYZ");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals(21, json.getInt("id"), errorMessage);
        Assertions.assertEquals("abcXYZ", json.getString("name"), errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "/two-complex/21/abcXYZ"), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(
                TestUtil.createPath(true, applicationPath(), resourcePath(), "two-complex/{id:[0-9]+}/{name:[a-zA-Z]+}"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Tests that a POST request returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/form-param}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void post() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Form form = new Form();
            form.param("name", "Salvatore");
            final URI uri = generateUri("form-param");
            try (Response response = client.target(uri).request().post(Entity.form(form))) {
                final var json = resolveJsonEntity(uri, response, 200);
                final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
                Assertions.assertEquals("Salvatore", json.getString("name"), errorMessage);
                Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "form-param"), json.getString("path"),
                        errorMessage);
                Assertions.assertEquals(TestUtil.createPath(true, applicationPath(), resourcePath(), "form-param"),
                        json.getString("resourceTemplate"), errorMessage);
            }
        }
    }

    /**
     * Tests that a PUT request returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/entity-param}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void put() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final List<EntityPart> entityParts = List.of(
                    EntityPart.withName("name")
                            .content("Rosa")
                            .build());
            final URI uri = generateUri("entity-param");
            try (
                    Response response = client.target(uri).request()
                            .put(Entity.entity(new GenericEntity<>(entityParts) {
                            }, MediaType.MULTIPART_FORM_DATA))) {
                final var json = resolveJsonEntity(uri, response, 201);
                final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
                Assertions.assertEquals("name", json.getString("entityPartName"), errorMessage);
                Assertions.assertEquals("Rosa", json.getString("content"), errorMessage);
                Assertions.assertEquals("name", json.getString("entityPartName"), errorMessage);
                Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "entity-param"), json.getString("path"),
                        errorMessage);
                Assertions.assertEquals(TestUtil.createPath(true, applicationPath(), resourcePath(), "entity-param"),
                        json.getString("resourceTemplate"), errorMessage);
            }
        }
    }

    /**
     * Tests that a simple path on a sub-resource returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/sub-resource/sub-simple}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subResourceSimple() throws Exception {
        final var json = get("sub-resource/sub-simple");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "sub-resource/sub-simple"), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, applicationPath(), resourcePath(), "sub-resource/sub-simple"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Tests that a complex path on a sub-resource returns the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/sub-resource/sub-complex/{id:[a-m][n-z][0-9]+}}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subResourceComplex() throws Exception {
        final var json = get("sub-resource/sub-complex/az200");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals("az200", json.getString("id"), errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "sub-resource/sub-complex/az200"),
                json.getString("path"),
                errorMessage);
        Assertions.assertEquals(
                TestUtil.createPath(true, applicationPath(), resourcePath(), "sub-resource/sub-complex/{id:[a-m][n-z][0-9]+}"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Tests that a two complex paths on a sub-resource return the expected resource template. The template value should be
     * {@code ${applicationPath}/${resourcePath}/sub-resource/sub-two-complex/{id:[0-9]+}/{name:[a-zA-Z]+}}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subResourceTwoComplex() throws Exception {
        final var json = get("sub-resource/sub-two-complex/2000/Mike");
        final Supplier<String> errorMessage = () -> String.format("Response: %s", json);
        Assertions.assertEquals(2000, json.getInt("id"), errorMessage);
        Assertions.assertEquals("Mike", json.getString("name"), errorMessage);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "sub-resource/sub-two-complex/2000/Mike"),
                json.getString("path"),
                errorMessage);
        Assertions.assertEquals(
                TestUtil.createPath(true, applicationPath(), resourcePath(),
                        "sub-resource/sub-two-complex/{id:[0-9]+}/{name:[a-zA-Z]+}"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Make two requests. The first has no arguments and should be cached. The second should hit the cache to ensure the
     * matchedResourceTemplate is set.
     *
     * @throws Exception if a test error occurs
     */
    @Test
    public void twoRequests() throws Exception {
        var json = get("simple");
        json = get("simple");
        final var errorMessage = String.format("Response: %s", json);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "simple"), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(
                TestUtil.createPath(true, applicationPath(), resourcePath(), "simple"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Make two requests. The first has no arguments and should be cached. The second should hit the cache to ensure the
     * matchedResourceTemplate is set.
     *
     * @throws Exception if a test error occurs
     */
    @Test
    public void subResourceTwoRequests() throws Exception {
        var json = get("sub-resource/sub-simple");
        json = get("sub-resource/sub-simple");
        final var errorMessage = String.format("Response: %s", json);
        Assertions.assertEquals(TestUtil.createPath(true, resourcePath(), "sub-resource/sub-simple"), json.getString("path"),
                errorMessage);
        Assertions.assertEquals(
                TestUtil.createPath(true, applicationPath(), resourcePath(), "sub-resource/sub-simple"),
                json.getString("resourceTemplate"), errorMessage);
    }

    /**
     * Returns the applications path. Must not return {@code null}.
     *
     * @return the applications path
     */
    protected abstract String applicationPath();

    /**
     * Returns the path of the resource. Must not return {@code null}.
     *
     * @return the path of the resource
     */
    protected abstract String resourcePath();

    private JsonObject get(final String path) throws URISyntaxException {
        try (Client client = ClientBuilder.newClient()) {
            final URI uri = generateUri(path);
            try (Response response = client.target(uri).request().get()) {
                return resolveJsonEntity(uri, response, 200);
            }
        }
    }

    private JsonObject resolveJsonEntity(final URI uri, final Response response, final int expectedStatus) {
        Assertions.assertEquals(expectedStatus, response.getStatus(),
                () -> String.format("Failed response from %s: %s", uri, response.readEntity(String.class)));
        final JsonObject json = response.readEntity(JsonObject.class);
        Assertions.assertNotNull(json);
        Assertions.assertNotNull(json.get("path"),
                () -> String.format("The path is not available in response %s", json));
        Assertions.assertNotNull(json.get("resourceTemplate"),
                () -> String.format("The resourceTemplate is not available in response %s", json));
        return json;
    }

    private URI generateUri(final String path) throws URISyntaxException {
        final UriBuilder uriBuilder = UriBuilder.fromUri(uri)
                .path(applicationPath())
                .path(resourcePath())
                .path(path);
        return uriBuilder.build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    public interface UriInfoResource {

        @GET
        @Path("")
        JsonObject empty();

        @GET
        @Path("/simple")
        JsonObject simple();

        @GET
        @Path("/complex/{id:[a-m][n-z][0-9]+}")
        JsonObject complex(@PathParam("id") String id);

        @GET
        @Path("two-complex/{id:[0-9]+}/{name:[a-zA-Z]+}")
        JsonObject complex(@PathParam("id") int id, @PathParam("name") String name);

        @Path("sub-resource/")
        SubResource subResource();

        @POST
        @Path("form-param")
        JsonObject formParam(@FormParam("name") String name);

        @PUT
        @Path("entity-param")
        Response entityForm(@FormParam("name") EntityPart entityPart) throws IOException;
    }

    public interface SubResource {

        @GET
        @Path("/sub-simple")
        JsonObject simple();

        @GET
        @Path("/sub-complex/{id:[a-m][n-z][0-9]+}")
        JsonObject complex(@PathParam("id") String id);

        @GET
        @Path("sub-two-complex/{id:[0-9]+}/{name:[a-zA-Z]+}")
        JsonObject complex(@PathParam("id") int id, @PathParam("name") String name);
    }

    public abstract static class AbstractUriInfoResource implements UriInfoResource {

        @Inject
        private UriInfo uriInfo;
        @Inject
        private SubResource subResource;

        @Override
        public JsonObject empty() {
            return createDefaultResponse().build();
        }

        @Override
        public JsonObject simple() {
            return createDefaultResponse().build();
        }

        @Override
        public JsonObject complex(final String id) {
            return createDefaultResponse()
                    .add("id", id)
                    .build();
        }

        @Override
        public JsonObject complex(final int id, final String name) {
            return createDefaultResponse()
                    .add("id", id)
                    .add("name", name)
                    .build();
        }

        @Override
        public SubResource subResource() {
            return subResource;
        }

        @Override
        public JsonObject formParam(final String name) {
            return createDefaultResponse()
                    .add("name", name)
                    .build();
        }

        @Override
        public Response entityForm(final EntityPart entityPart) throws IOException {
            final var entity = createDefaultResponse()
                    .add("entityPartName", entityPart.getName())
                    .add("content", entityPart.getContent(String.class))
                    .build();
            return Response.status(Response.Status.CREATED).entity(entity).build();
        }

        protected JsonObjectBuilder createDefaultResponse() {
            return Json.createObjectBuilder()
                    .add("path", uriInfo.getPath())
                    .add("resourceTemplate", uriInfo.getMatchedResourceTemplate());
        }
    }

    @RequestScoped
    public static class DefaultSubResource implements SubResource {

        @Inject
        private UriInfo uriInfo;

        @Override
        public JsonObject simple() {
            return createDefaultResponse().build();
        }

        @Override
        public JsonObject complex(final String id) {
            return createDefaultResponse()
                    .add("id", id)
                    .build();
        }

        @Override
        public JsonObject complex(final int id, final String name) {
            return createDefaultResponse()
                    .add("id", id)
                    .add("name", name)
                    .build();
        }

        protected JsonObjectBuilder createDefaultResponse() {
            return Json.createObjectBuilder()
                    .add("path", uriInfo.getPath())
                    .add("resourceTemplate", uriInfo.getMatchedResourceTemplate());
        }
    }
}
