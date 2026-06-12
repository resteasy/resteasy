/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.patch;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for JsonMergePatch as a direct method parameter.
 * Verifies that JsonMergePatch can be automatically deserialized
 * when using @Consumes("application/merge-patch+json").
 *
 * @see <a href="https://tools.ietf.org/html/rfc7386">JSON Merge Patch</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JsonMergePatchParameterTest {

    @ArquillianResource
    private Deployer deployer;

    private static Client client;
    private static final String DEPLOYMENT_NAME = "JsonMergePatchParameter";
    private static final Student testStudentEntity = new Student()
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setSchool("University A")
            .setGender("Male");

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    @BeforeEach
    public void beforeEach() {
        deployer.deploy(DEPLOYMENT_NAME);
        createStudentEntityOnServerAndValidate(testStudentEntity, DEPLOYMENT_NAME);
    }

    @AfterEach
    public void afterEach() {
        deployer.undeploy(DEPLOYMENT_NAME);
    }

    @Deployment(managed = false, name = DEPLOYMENT_NAME)
    public static Archive<?> createDeployment() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_NAME);
        return TestUtil.finishContainerPrepare(war, null,
                JsonMergePatchParameterResource.class, Student.class);
    }

    /**
     * Test that JsonMergePatch can be used directly as a method parameter.
     *
     * <ul>
     * <li>Create a student entity on the server</li>
     * <li>Verify the entity has expected form</li>
     * <li>Send a merge-patch+json request with {"lastName": "Smith", "school": "MIT"} value</li>
     * <li>Verify that the JsonMergePatch parameter was correctly deserialized and applied</li>
     * </ul>
     */
    @Test
    public void testJsonMergePatchParameter() {
        // Create a new student for this test
        Student newStudent = new Student()
                .setId(100L)
                .setFirstName("Alice")
                .setLastName("Johnson")
                .setSchool("Tech University")
                .setGender("Female");

        // Create the student on the server
        createStudentEntityOnServerAndValidate(newStudent, DEPLOYMENT_NAME);

        long id = newStudent.getId();

        // Verify initial state
        Student student = getStudentFromServer(id, DEPLOYMENT_NAME);
        Assertions.assertEquals("Alice", student.getFirstName());
        Assertions.assertEquals("Johnson", student.getLastName());
        Assertions.assertEquals("Tech University", student.getSchool());
        Assertions.assertEquals("Female", student.getGender());

        // Create merge patch JSON
        JsonObject patchJson = Json.createObjectBuilder()
                .add("lastName", "Smith")
                .add("school", "MIT")
                .build();

        // Apply patch using JsonMergePatch parameter
        Response response = patchStudentWithJsonMergePatch(id, patchJson.toString(), DEPLOYMENT_NAME);

        Assertions.assertEquals(200, response.getStatus());

        Student patchedStudent = response.readEntity(Student.class);

        // Verify the patch was applied correctly
        Assertions.assertEquals("Alice", patchedStudent.getFirstName(), "firstName should remain unchanged");
        Assertions.assertEquals("Smith", patchedStudent.getLastName(), "lastName should be updated to Smith");
        Assertions.assertEquals("MIT", patchedStudent.getSchool(), "school should be updated to MIT");
        Assertions.assertEquals("Female", patchedStudent.getGender(), "gender should remain unchanged");
    }

    /**
     * Test that JsonMergePatch parameter handles nested objects correctly.
     */
    @Test
    public void testJsonMergePatchParameterWithNestedObject() {
        long id = testStudentEntity.getId();

        // Create merge patch with nested mentor object
        JsonObject patchJson = Json.createObjectBuilder()
                .add("mentor", Json.createObjectBuilder()
                        .add("firstName", "Jane")
                        .add("lastName", "Smith")
                        .build())
                .build();

        Response response = patchStudentWithJsonMergePatch(id, patchJson.toString(), DEPLOYMENT_NAME);
        Assertions.assertEquals(200, response.getStatus());

        Student patchedStudent = response.readEntity(Student.class);

        // Verify nested object was set
        Assertions.assertNotNull(patchedStudent.getMentor(), "mentor should not be null");
        Assertions.assertEquals("Jane", patchedStudent.getMentor().getFirstName());
        Assertions.assertEquals("Smith", patchedStudent.getMentor().getLastName());
    }

    /**
     * Test that JsonMergePatch parameter handles empty patch correctly.
     */
    @Test
    public void testJsonMergePatchParameterWithEmptyPatch() {
        long id = testStudentEntity.getId();

        // Create empty merge patch
        JsonObject patchJson = Json.createObjectBuilder().build();

        Response response = patchStudentWithJsonMergePatch(id, patchJson.toString(), DEPLOYMENT_NAME);
        Assertions.assertEquals(200, response.getStatus());

        Student patchedStudent = response.readEntity(Student.class);

        // Verify nothing changed
        Assertions.assertEquals(testStudentEntity.getFirstName(), patchedStudent.getFirstName());
        Assertions.assertEquals(testStudentEntity.getLastName(), patchedStudent.getLastName());
        Assertions.assertEquals(testStudentEntity.getSchool(), patchedStudent.getSchool());
    }

    private void createStudentEntityOnServerAndValidate(Student student, String deploymentName) {
        WebTarget postTarget = client.target(generateURL("/students", deploymentName));
        Response response = postTarget.request().post(Entity.entity(student, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, response.getStatus());
        Student responseStudent = response.readEntity(Student.class);
        Assertions.assertEquals(student, responseStudent);
    }

    private Student getStudentFromServer(long id, String deploymentName) {
        WebTarget getTarget = client.target(generateURL("/students/" + id, deploymentName));
        Response response = getTarget.request().get();
        Assertions.assertEquals(200, response.getStatus());
        return response.readEntity(Student.class);
    }

    private Response patchStudentWithJsonMergePatch(long id, String patchJson, String deploymentName) {
        WebTarget patchTarget = client.target(generateURL("/students/" + id, deploymentName));
        return patchTarget.request().build(HttpMethod.PATCH,
                Entity.entity(patchJson, "application/merge-patch+json")).invoke();
    }

    private String generateURL(String path, String deploymentName) {
        return PortProviderUtil.generateURL(path, deploymentName);
    }
}
