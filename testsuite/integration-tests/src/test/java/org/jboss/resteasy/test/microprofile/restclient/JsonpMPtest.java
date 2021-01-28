package org.jboss.resteasy.test.microprofile.restclient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.microprofile.client.RestClientBuilderImpl;
import org.jboss.resteasy.test.microprofile.restclient.resource.JsonpMPService;
import org.jboss.resteasy.test.microprofile.restclient.resource.JsonpMPServiceIntf;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import java.net.URI;

/**
 * @tpSubChapter MicroProfile rest client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Show JSON-P is supported.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonpMPtest {
    protected static final Logger LOG = Logger.getLogger(JsonpMPtest.class.getName());
    private static final String WAR_SERVICE = "jsonP_service";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(JsonpMPService.class);
      return TestUtil.finishContainerPrepare(war, null, null);
    }

    static JsonpMPServiceIntf jsonpMPServiceIntf;
    @Before
    public void before() throws Exception {
        RestClientBuilderImpl builder = new RestClientBuilderImpl();
        jsonpMPServiceIntf = builder
                .baseUri(URI.create(generateURL("", WAR_SERVICE)))
                .build(JsonpMPServiceIntf.class);
    }

    private static String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    @Test
    public void testObject() {

        JsonObject obj = Json.createObjectBuilder()
                .add("name", "Bill")
                .add("id", 10001)
                .build();

        JsonObject response = jsonpMPServiceIntf.object(obj);
        Assert.assertTrue("JsonObject from the response doesn't contain field 'name'",
                response.containsKey("name"));
        Assert.assertEquals("JsonObject from the response doesn't contain correct value for the field 'name'",
                response.getJsonString("name").getString(), "Bill");
        Assert.assertTrue("JsonObject from the response doesn't contain field 'id'",
                response.containsKey("id"));
        Assert.assertEquals("JsonObject from the response doesn't contain correct value for the field 'id'",
                response.getJsonNumber("id").longValue(), 10001);
    }

    @Test
    public void testStructure() {
        JsonStructure structure = (JsonStructure) Json.createObjectBuilder().add("name", "Bill").build();
        JsonStructure response = jsonpMPServiceIntf.object(structure);
        JsonObject obj = (JsonObject) response;
        Assert.assertTrue("JsonObject from the response doesn't contain field 'name'",
                obj.containsKey("name"));
        Assert.assertEquals("JsonObject from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Bill");
    }

    @Test
    public void testJsonNumber() {
        JsonNumber jsonNumber = Json.createValue(100);
        JsonNumber response = jsonpMPServiceIntf.testNumber(jsonNumber);
        Assert.assertTrue("JsonNumber object with 200 value is expected",
                response.intValue() == 200);
    }

    @Test
    public void testArray() {
        JsonArray array = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("name", "Bill").build())
                .add(Json.createObjectBuilder().add("name", "Monica").build())
                .build();

        JsonArray response = jsonpMPServiceIntf.array(array);
        Assert.assertEquals("JsonArray from the response doesn't contain two elements as it should",
                2, response.size());
        JsonObject obj = response.getJsonObject(0);
        Assert.assertTrue("JsonObject[0] from the response doesn't contain field 'name'",
                obj.containsKey("name"));
        Assert.assertEquals("JsonObject[0] from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Bill");
        obj = response.getJsonObject(1);
        Assert.assertTrue("JsonObject[1] from the response doesn't contain field 'name'",
                obj.containsKey("name"));
        Assert.assertEquals("JsonObject[1] from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Monica");
    }

    @Test
    public void testJsonString() throws Exception {

        JsonString jsonString = Json.createValue("Resteasy");
        JsonString  response = jsonpMPServiceIntf.testString(jsonString);

        Assert.assertTrue("JsonString object with Hello Resteasy value is expected",
                response.getString().equals("Hello Resteasy"));
    }
}
