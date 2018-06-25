package org.jboss.resteasy.test.resource.patch;

import javax.json.Json;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class StudentPatchTest {

    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
        client = null;
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(StudentPatchTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, StudentResource.class, Student.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, StudentPatchTest.class.getSimpleName());
    }

    @Test
    //@Ignore
    public void testPatchStudent() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().connectionPoolSize(10).build();

        WebTarget base = client.target(generateURL("/students"));
        //add a student, first name is Taylor and school is school1, other fields is null.
        Student newStudent = new Student().setId(1L).setFirstName("Taylor").setSchool("school1");
        Response response = base.request().post(Entity.<Student>entity(newStudent, MediaType.APPLICATION_JSON_TYPE));
        Student s = response.readEntity(Student.class);
        Assert.assertNotNull("Add student failed", s);
        Assert.assertEquals("Taylor", s.getFirstName());
        Assert.assertNull("Last name is not null", s.getLastName());
        Assert.assertEquals("school1", s.getSchool());
        Assert.assertNull("Gender is not null", s.getGender());

        //patch a student, after patch we can get a male student named John Taylor and school is null.
        WebTarget patchTarget = client.target(generateURL("/students/1"));
        javax.json.JsonArray patchRequest = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("op", "copy").add("from", "/firstName").add("path", "/lastName").build())
                .add(Json.createObjectBuilder().add("op", "replace").add("path", "/firstName").add("value", "John").build())
                .add(Json.createObjectBuilder().add("op", "remove").add("path", "/school").build())
                .add(Json.createObjectBuilder().add("op", "add").add("path", "/gender").add("value", "male").build())
                .build();
        patchTarget.request().build(HttpMethod.PATCH, Entity.entity(patchRequest, MediaType.APPLICATION_JSON_PATCH_JSON)).invoke();

        //verify the patch update result
        WebTarget getTarget = client.target(generateURL("/students/1"));
        Response getResponse = getTarget.request().get();
        Student patchedStudent = getResponse.readEntity(Student.class);
        Assert.assertEquals("Expected lastname is changed to Taylor", "Taylor", patchedStudent.getLastName());
        Assert.assertEquals("Expected firstname is replaced from Taylor to John", "John", patchedStudent.getFirstName());
        Assert.assertEquals("Expected school is null", null, patchedStudent.getSchool());
        Assert.assertEquals("Add gender", "male", patchedStudent.getGender());
    }
}
