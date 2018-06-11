package org.jboss.resteasy.test.providers.jsonp;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.jsonp.resource.JsonpResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * @tpSubChapter Json-p provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonpTest {

    protected static final Logger logger = Logger.getLogger(JsonpTest.class.getName());

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonpTest.class.getSimpleName());
        war.addClass(JsonpTest.class);
        return TestUtil.finishContainerPrepare(war, null, JsonpResource.class);
    }

    @Before
    public void init() {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonpTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends POST request with JsonObject entity. The JsonObject should be returned back by the
     * response and should contain the same field values as original request.
     * in the second case multiple json entities as String.
     * @tpPassCrit The resource returns JsonObject with correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testObject() throws Exception {
       doTestObject("UTF-8");
       doTestObject("UTF-16");
       doTestObject("UTF-32");
       doTestObject(null);
    }
    
    private void doTestObject(String charset) throws Exception {
        WebTarget target = client.target(generateURL("/test/json/object"));
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
        Entity<String> entity = Entity.entity("{ \"name\" : \"Bill\" }", mediaType);
        String json = target.request().post(entity, String.class);
        logger.info("Request entity: " + json);

        JsonObject obj = Json.createObjectBuilder().add("name", "Bill").add("id", 10001).build();
        
        obj = target.request().post(Entity.json(obj), JsonObject.class);
        Assert.assertTrue("JsonObject from the response doesn't contain field 'name'", obj.containsKey("name"));
        Assert.assertEquals("JsonObject from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Bill");
        Assert.assertTrue("JsonObject from the response doesn't contain field 'id'", obj.containsKey("id"));
        Assert.assertEquals("JsonObject from the response doesn't contain correct value for the field 'id'",
              obj.getJsonNumber("id").longValue(), 10001);
    }

    /**
     * @tpTestDetails Client sends POST request with JsonArray entity. The JsonArray should be returned back by the
     * response and should contain the same field values as original request.
     * in the second case multiple json entities as String.
     * @tpPassCrit The resource returns JsonArray with correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testArray() throws Exception {
       doTestArray("UTF-8");
       doTestArray("UTF-16");
       doTestArray("UTF-32");
       doTestArray(null);
    }
    
    private void doTestArray(String charset) {
        WebTarget target = client.target(generateURL("/test/json/array"));
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
        Entity<String> entity = Entity.entity("[{ \"name\" : \"Bill\" },{ \"name\" : \"Monica\" }]", mediaType);
        String json = target.request().post(entity, String.class);//        String json = target.request().post(Entity.json("[{ \"name\" : \"Bill\" },{ \"name\" : \"Monica\" }]"), String.class);
        logger.info("Request entity: " + json);

        JsonArray array = Json.createArrayBuilder().add(Json.createObjectBuilder().add("name", "Bill").build())
                .add(Json.createObjectBuilder().add("name", "Monica").build()).build();
        array = target.request().post(Entity.json(array), JsonArray.class);
        Assert.assertEquals("JsonArray from the response doesn't contain two elements as it should", 2, array.size());
        JsonObject obj = array.getJsonObject(0);
        Assert.assertTrue("JsonObject[0] from the response doesn't contain field 'name'", obj.containsKey("name"));
        Assert.assertEquals("JsonObject[0] from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Bill");
        obj = array.getJsonObject(1);
        Assert.assertTrue("JsonObject[1] from the response doesn't contain field 'name'", obj.containsKey("name"));
        Assert.assertEquals("JsonObject[1] from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Monica");

    }

    /**
     * @tpTestDetails Client sends POST request with JsonStructure entity. The JsonStructure should be returned back by the
     * response and should contain the same field values as original request.
     * in the second case multiple json entities as String.
     * @tpPassCrit The resource returns JsonStructure with correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStructure() throws Exception {
       doTestStructure("UTF-8");
       doTestStructure("UTF-16");
       doTestStructure("UTF-32");
       doTestStructure(null);
    }

    @Test
    public void testJsonString() throws Exception {
       WebTarget target = client.target(generateURL("/test/json/string"));
       JsonString jsonString = Json.createValue("Resteasy");
       JsonString result = target.request().post(Entity.json(jsonString), JsonString.class);
       Assert.assertTrue("JsonString object with Hello Resteasy value is expected", result.getString().equals("Hello Resteasy"));
    }

    @Test
    public void testJsonNumber() throws Exception {
       WebTarget target = client.target(generateURL("/test/json/number"));
       JsonNumber jsonNumber = Json.createValue(100);
       JsonNumber result = target.request().post(Entity.json(jsonNumber), JsonNumber.class);
       Assert.assertTrue("JsonNumber object with 200 value is expected", result.intValue() == 200);
    }

    private void doTestStructure(String charset) {
        WebTarget target = client.target(generateURL("/test/json/structure"));
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
        Entity<String> entity = Entity.entity("{ \"name\" : \"Bill\" }", mediaType);
        String json = target.request().post(entity, String.class);
        logger.info("Request entity: " + json);

        JsonStructure str = (JsonStructure) Json.createObjectBuilder().add("name", "Bill").build();
        JsonStructure structure = target.request().post(Entity.json(str), JsonStructure.class);
        JsonObject obj = (JsonObject) structure;
        Assert.assertTrue("JsonObject from the response doesn't contain field 'name'", obj.containsKey("name"));
        Assert.assertEquals("JsonObject from the response doesn't contain correct value for the field 'name'",
                obj.getJsonString("name").getString(), "Bill");
    }
}
