package org.jboss.resteasy.test.resource.patch;

import jakarta.json.Json;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PatchErrorHandlingTest {
    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PatchErrorHandlingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, StudentResource.class, Student.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PatchErrorHandlingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails When the server determines that the patch document provided by the client is not properly formatted,
     *                it SHOULD return a 400 (Bad Request) response.
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testMalformedPatchDocument() throws Exception {
        WebTarget base = client.target(generateURL("/students"));
        Student newStudent = new Student().setId(1L).setFirstName("Taylor").setSchool("school1");
        base.request().post(Entity.entity(newStudent, MediaType.APPLICATION_JSON_TYPE));

        WebTarget patchTarget = client.target(generateURL("/students/1"));
        jakarta.json.JsonArray patchRequest = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("op", "copyyy").add("from", "/firstName").add("path", "/lastName").build())
                .build();
        Response res = patchTarget.request()
                .build(HttpMethod.PATCH, Entity.entity(patchRequest, MediaType.APPLICATION_JSON_PATCH_JSON)).invoke();
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, res.getStatus());
    }

    /**
     * @tpTestDetails Client sends PATCH request in the format which is not supported for the resource.
     *                Server should return 415 (Unsupported media type)
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testUnsupportedPatchDocument() throws Exception {
        WebTarget patchTarget = client.target(generateURL("/students/1"));
        Student student = new Student().setFirstName("test");
        Response res = patchTarget.request().build(HttpMethod.PATCH, Entity.entity(student, MediaType.APPLICATION_JSON_TYPE))
                .invoke();
        Assertions.assertEquals(HttpResponseCodes.SC_UNSUPPORTED_MEDIA_TYPE, res.getStatus());
    }

    /**
     * @tpTestDetails Client sends valid Patch request with with valid format descriptor, but the resource doesn't exists.
     *                Server should return 404 (Not found).
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testResourceNotFound() throws Exception {
        WebTarget base = client.target(generateURL("/students"));
        Student newStudent = new Student().setId(1L).setFirstName("Taylor").setSchool("school1");
        base.request().post(Entity.entity(newStudent, MediaType.APPLICATION_JSON_TYPE));

        WebTarget patchTarget = client.target(generateURL("/students/1088"));
        jakarta.json.JsonArray patchRequest = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("op", "copy").add("from", "/firstName").add("path", "/lastName").build())
                .build();
        Response res = patchTarget.request()
                .build(HttpMethod.PATCH, Entity.entity(patchRequest, MediaType.APPLICATION_JSON_PATCH_JSON)).invoke();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, res.getStatus());
    }

    /**
     * @tpTestDetails Client sends Patch request to patch property of the object which doesn't exists.
     *                Server should return 409 (Conflict)
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testConflictingState() throws Exception {
        WebTarget base = client.target(generateURL("/students"));
        Student newStudent = new Student().setId(1L).setFirstName("Taylor").setSchool("school1");
        base.request().post(Entity.entity(newStudent, MediaType.APPLICATION_JSON_TYPE));

        WebTarget patchTarget = client.target(generateURL("/students/1"));
        jakarta.json.JsonArray patchRequest = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("op", "replace").add("path", "/wrongProperty").add("value", "John").build())
                .build();
        Response res = patchTarget.request()
                .build(HttpMethod.PATCH, Entity.entity(patchRequest, MediaType.APPLICATION_JSON_PATCH_JSON)).invoke();
        Assertions.assertEquals(HttpResponseCodes.SC_CONFLICT, res.getStatus());
    }
}
