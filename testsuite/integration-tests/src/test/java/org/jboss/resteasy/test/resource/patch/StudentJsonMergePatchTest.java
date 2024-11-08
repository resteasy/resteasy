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

package org.jboss.resteasy.test.resource.patch;

import jakarta.json.Json;
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
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
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
 * Tests related to JSON Merge Patch
 *
 * @see <a href="https://tools.ietf.org/html/rfc7386">JSON Merge Patch</a>
 * @see <a href="https://issues.redhat.com/browse/RESTEASY-2567">RESTEASY-2567</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class StudentJsonMergePatchTest {

    private static final Logger log = Logger.getLogger(StudentJsonMergePatchTest.class);

    @ArquillianResource
    private Deployer deployer;

    private static Client client;
    private static final String PATCH_DEPLOYMENT = "Patch";
    private static final Student testStudentEntity = new Student()
            .setId(2L)
            .setFirstName("Alice")
            .setSchool("school2");

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
        deployer.deploy(PATCH_DEPLOYMENT);
        createStudentEntityOnServerAndValidate(testStudentEntity, PATCH_DEPLOYMENT);
    }

    @AfterEach
    public void afterEach() {
        deployer.undeploy(PATCH_DEPLOYMENT);
    }

    @Deployment(managed = false, name = PATCH_DEPLOYMENT)
    public static Archive<?> createDeployment() {
        WebArchive war = TestUtil.prepareArchive(PATCH_DEPLOYMENT);
        return TestUtil.finishContainerPrepare(war, null, StudentResource.class, Student.class);
    }

    private String generateURL(String path, String deploymentName) {
        return PortProviderUtil.generateURL(path, deploymentName);
    }

    /**
     * <p>
     * Test patching an existing entity.
     * </p>
     * Test procedure:
     * <ul>
     * <li>Create a {@link Student} entity with ID, first name and school.</li>
     * <li>Verify the entity has expected form.</li>
     * <li>Send a merge-patch+json request with {"lastName": "Green", "school": null} value</li>
     * <li>Verify that value for school got removed, last name got set from null to "Green" and other values were
     * not touched</li>
     * </ul>
     */
    @Test
    @OperateOnDeployment(PATCH_DEPLOYMENT)
    public void testJSONPMergePatchStudent() {
        final Student patchedStudent = mergePatchJsonStudentEntity(testStudentEntity.getId(),
                Json.createObjectBuilder()
                        .add("lastName", "Green")
                        .addNull("school")
                        .build(),
                PATCH_DEPLOYMENT);
        Assertions.assertEquals("Green", patchedStudent.getLastName(), "Expected lastname is changed to Green");
        Assertions.assertEquals("Alice", patchedStudent.getFirstName(), "Expected firstname is Alice");
        Assertions.assertNull(patchedStudent.getSchool(), "Expected school is null");
        Assertions.assertNull(patchedStudent.getGender(), "Expected gender is null");
    }

    /**
     * <p>
     * Test applying a patch which has a key (with a value) which cannot be mapped to any field in the entity.
     * </p>
     * Test procedure:
     * <ul>
     * <li>Create a {@link Student} entity with ID, first name and school.</li>
     * <li>Verify the entity has expected form.</li>
     * <li>Send a merge-patch+json request with {"nonMappableKey": "Foo"} value</li>
     * <li>Verify that nothing was changed in the entity.</li>
     * </ul>
     */
    @Test
    @OperateOnDeployment(PATCH_DEPLOYMENT)
    public void testJSONMergePatchStudentNonExistingKeyToValue() {
        final Student patchedStudent = mergePatchJsonStudentEntity(testStudentEntity.getId(),
                Json.createObjectBuilder()
                        .add("nonMappableKey", "Foo")
                        .build(),
                PATCH_DEPLOYMENT);
        Assertions.assertEquals(testStudentEntity, patchedStudent, "We should receive same entity since no patch was applied.");
    }

    /**
     * <p>
     * Test applying a patch which has a key (without a value) which cannot be mapped to any field in the entity.
     * </p>
     * Test procedure:
     * <ul>
     * <li>Create a {@link Student} entity with ID, first name and school.</li>
     * <li>Verify the entity has expected form.</li>
     * <li>Send a merge-patch+json request with {"nonMappableKey": null} value</li>
     * <li>Verify that nothing was changed in the entity.</li>
     * </ul>
     */

    @Test
    @OperateOnDeployment(PATCH_DEPLOYMENT)
    public void testJSONMergePatchStudentNonExistingKeyToNull() {
        final Student patchedStudent = mergePatchJsonStudentEntity(testStudentEntity.getId(),
                Json.createObjectBuilder()
                        .addNull("nonMappableKey")
                        .build(),
                PATCH_DEPLOYMENT);
        Assertions.assertEquals(testStudentEntity, patchedStudent, "We should receive same entity since no patch was applied.");
    }

    /**
     * <p>
     * Test that trying to patch a key to a non-mappable value (string to an object) ends up in an error/
     * </p>
     * Test procedure:
     * <ul>
     * <li>Create a {@link Student} entity with ID, first name and school.</li>
     * <li>Verify the entity has expected form.</li>
     * <li>Send a merge-patch+json request with {"school": {"foo": "bar"}} value</li>
     * <li>Verify that the response had status 500</li>
     * <li>Verify that server entity was not affected</li>
     * </ul>
     */
    @Test
    @OperateOnDeployment(PATCH_DEPLOYMENT)
    public void testJSONMergePatchStudentNonMappableTypeToExistingKey() {
        final Response response = mergePatchJsonStudentEntityForResponse(testStudentEntity.getId(),
                Json.createObjectBuilder()
                        .add("school", Json.createObjectBuilder().add("foo", "bar").build())
                        .build(),
                PATCH_DEPLOYMENT);
        Assertions.assertEquals(500, response.getStatus());

        final Student serverSideStudent = client.target(generateURL("/students/" + testStudentEntity.getId(), PATCH_DEPLOYMENT))
                .request()
                .get()
                .readEntity(Student.class);
        Assertions.assertEquals(testStudentEntity, serverSideStudent, "Server side entity got edited!");
    }

    private Student mergePatchJsonStudentEntity(final Long id, final JsonObject patch, final String deploymentName) {
        return mergePatchJsonStudentEntityForResponse(id, patch, deploymentName).readEntity(Student.class);
    }

    private Response mergePatchJsonStudentEntityForResponse(final Long id, final JsonObject patch,
            final String deploymentName) {
        WebTarget patchTarget = client.target(generateURL("/students/" + id, deploymentName));
        return patchTarget.request().build(HttpMethod.PATCH, Entity.entity(patch, "application/merge-patch+json"))
                .invoke();
    }

    private void createStudentEntityOnServerAndValidate(final Student student, final String deploymentName) {
        WebTarget base = client.target(generateURL("/students", deploymentName));
        Response response = base.request().post(Entity.<Student> entity(student, MediaType.APPLICATION_JSON_TYPE));
        final Student responseStudent = response.readEntity(Student.class);
        Assertions.assertEquals(student, responseStudent, "Student entity on server doesn't match local one");
    }

}
