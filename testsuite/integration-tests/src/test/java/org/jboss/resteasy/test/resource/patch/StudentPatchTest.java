package org.jboss.resteasy.test.resource.patch;

import java.util.HashMap;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class StudentPatchTest {

    static Client client;
    static final String PATCH_DEPLOYMENT = "Patch";
    static final String DISABLED_PATCH_DEPLOYMENT = "DisablePatch";
    static final String LEGACY_PATCH_DEPLOYMENT = "LegacyPatch";

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
        client = null;
    }

    @Deployment(name = PATCH_DEPLOYMENT, order = 1)
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PATCH_DEPLOYMENT);
        return TestUtil.finishContainerPrepare(war, null, StudentResource.class, Student.class);
    }

    @Deployment(name = LEGACY_PATCH_DEPLOYMENT, order = 2)
    public static Archive<?> deployLegacyFilter() {
        WebArchive war = TestUtil.prepareArchive(LEGACY_PATCH_DEPLOYMENT);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_PATCH_FILTER_LEGACY, "true");
        return TestUtil.finishContainerPrepare(war, contextParam, StudentResource.class, Student.class);
    }

    @Deployment(name = DISABLED_PATCH_DEPLOYMENT, order = 3)
    public static Archive<?> createDisablePatchFilterDeployment() {
        WebArchive war = TestUtil.prepareArchive(DISABLED_PATCH_DEPLOYMENT);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED, "true");
        return TestUtil.finishContainerPrepare(war, contextParam, StudentResource.class, Student.class);
    }

    private String generateURL(String path, String deploymentName) {
        return PortProviderUtil.generateURL(path, deploymentName);
    }

    @Test
    @OperateOnDeployment(PATCH_DEPLOYMENT)
    public void testPatchStudent() throws Exception {
        testPatch(PATCH_DEPLOYMENT);
    }

    @Test
    @OperateOnDeployment(PATCH_DEPLOYMENT)
    public void testMergePatchStudent() throws Exception {
        testMergePatch(PATCH_DEPLOYMENT);
    }

    @Test
    @OperateOnDeployment(LEGACY_PATCH_DEPLOYMENT)
    public void testJSONPPatchStudent() throws Exception {
        testPatch(LEGACY_PATCH_DEPLOYMENT);
    }

    @Test
    @OperateOnDeployment(LEGACY_PATCH_DEPLOYMENT)
    public void testJSONPMergePatchStudent() throws Exception {
        testMergePatch(LEGACY_PATCH_DEPLOYMENT);
    }

    @Test
    @OperateOnDeployment(DISABLED_PATCH_DEPLOYMENT)
    @Ignore("RESTEASY-3132")
    public void testPatchDisabled() throws Exception {
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();

        WebTarget base = client.target(PortProviderUtil.generateURL("/students", DISABLED_PATCH_DEPLOYMENT));
        //add a student, first name is Taylor and school is school1, other fields is null.
        Student newStudent = new Student().setId(1L).setFirstName("Taylor").setSchool("school1");
        Response response = base.request().post(Entity.<Student> entity(newStudent, MediaType.APPLICATION_JSON_TYPE));
        Student s = response.readEntity(Student.class);
        Assert.assertNotNull("Add student failed", s);
        Assert.assertEquals("Taylor", s.getFirstName());
        Assert.assertNull("Last name is not null", s.getLastName());
        Assert.assertEquals("school1", s.getSchool());
        Assert.assertNull("Gender is not null", s.getGender());

        WebTarget patchTarget = client.target(PortProviderUtil.generateURL("/students/1", DISABLED_PATCH_DEPLOYMENT));
        jakarta.json.JsonArray patchRequest = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("op", "copy").add("from", "/firstName").add("path", "/lastName").build())
                .add(Json.createObjectBuilder().add("op", "replace").add("path", "/firstName").add("value", "John").build())
                .build();
        Response res = patchTarget.request()
                .build(HttpMethod.PATCH, Entity.entity(patchRequest, MediaType.APPLICATION_JSON_PATCH_JSON)).invoke();
        Assert.assertEquals("Http 400 is expected", 400, res.getStatus());
        client.close();
    }

    private void testPatch(String deployment) throws Exception {
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();

        WebTarget base = client.target(generateURL("/students", deployment));
        //add a student, first name is Taylor and school is school1, other fields is null.
        Student newStudent = new Student().setId(1L).setFirstName("Taylor").setSchool("school1");
        Response response = base.request().post(Entity.<Student> entity(newStudent, MediaType.APPLICATION_JSON_TYPE));
        Student s = response.readEntity(Student.class);
        Assert.assertNotNull("Add student failed", s);
        Assert.assertEquals("Taylor", s.getFirstName());
        Assert.assertNull("Last name is not null", s.getLastName());
        Assert.assertEquals("school1", s.getSchool());
        Assert.assertNull("Gender is not null", s.getGender());

        //patch a student, after patch we can get a male student named John Taylor and school is null.
        WebTarget patchTarget = client.target(generateURL("/students/1", deployment));
        jakarta.json.JsonArray patchRequest = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("op", "copy").add("from", "/firstName").add("path", "/lastName").build())
                .add(Json.createObjectBuilder().add("op", "replace").add("path", "/firstName").add("value", "John").build())
                .add(Json.createObjectBuilder().add("op", "remove").add("path", "/school").build())
                .add(Json.createObjectBuilder().add("op", "add").add("path", "/gender").add("value", "male").build())
                .build();
        patchTarget.request().build(HttpMethod.PATCH, Entity.entity(patchRequest, MediaType.APPLICATION_JSON_PATCH_JSON))
                .invoke();

        //verify the patch update result
        WebTarget getTarget = client.target(generateURL("/students/1", deployment));
        Response getResponse = getTarget.request().get();
        Student patchedStudent = getResponse.readEntity(Student.class);
        Assert.assertEquals("Expected lastname is changed to Taylor", "Taylor", patchedStudent.getLastName());
        Assert.assertEquals("Expected firstname is replaced from Taylor to John", "John", patchedStudent.getFirstName());
        Assert.assertEquals("Expected school is null", null, patchedStudent.getSchool());
        Assert.assertEquals("Add gender", "male", patchedStudent.getGender());
        client.close();
    }

    private void testMergePatch(String deployment) throws Exception {
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        WebTarget base = client.target(generateURL("/students", deployment));
        Student newStudent = new Student().setId(2L).setFirstName("Alice").setSchool("school2");
        Response response = base.request().post(Entity.<Student> entity(newStudent, MediaType.APPLICATION_JSON_TYPE));
        Student s = response.readEntity(Student.class);
        Assert.assertNotNull("Add student failed", s);
        Assert.assertEquals("Alice", s.getFirstName());
        Assert.assertNull("Last name is not null", s.getLastName());
        Assert.assertEquals("school2", s.getSchool());
        Assert.assertNull("Gender is not null", s.getGender());
        WebTarget patchTarget = client.target(generateURL("/students/2", deployment));
        JsonObject object = Json.createObjectBuilder().add("lastName", "Green").addNull("school").build();
        Response result = patchTarget.request().build(HttpMethod.PATCH, Entity.entity(object, "application/merge-patch+json"))
                .invoke();
        Student patchedStudent = result.readEntity(Student.class);
        Assert.assertEquals("Expected lastname is changed to Green", "Green", patchedStudent.getLastName());
        Assert.assertEquals("Expected firstname is Alice", "Alice", patchedStudent.getFirstName());
        Assert.assertEquals("Expected school is null", null, patchedStudent.getSchool());
        Assert.assertEquals("Expected gender is null", null, patchedStudent.getGender());
        client.close();
    }
}
