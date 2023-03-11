package org.jboss.resteasy.test.providers.jsonp.resource;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.junit.Assert;

@Path("/test/json")
public class JsonpResource {
    @Path("array")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public JsonArray array(JsonArray array) {
        Assert.assertEquals("The request didn't contain 2 json elements", 2, array.size());
        JsonObject obj = array.getJsonObject(0);
        Assert.assertTrue("The field 'name' didn't propagated correctly from the request for object[0]",
                obj.containsKey("name"));
        Assert.assertEquals("The value of field 'name' didn't propagated correctly from the request for object[0]",
                obj.getJsonString("name").getString(), "Bill");
        obj = array.getJsonObject(1);
        Assert.assertTrue("The field 'name' didn't propagated correctly from the request for object[1]",
                obj.containsKey("name"));
        Assert.assertEquals("The value of field 'name' didn't propagated correctly from the request for object[1]",
                obj.getJsonString("name").getString(), "Monica");
        return array;
    }

    @Path("object")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public JsonObject object(JsonObject obj) {
        Assert.assertTrue("The field 'name' didn't propagated correctly from the request", obj.containsKey("name"));
        Assert.assertEquals("The value of field 'name' didn't propagated correctly from the request",
                obj.getJsonString("name").getString(), "Bill");
        if (obj.containsKey("id")) {
            Assert.assertEquals("The value of field 'id' didn't propagated correctly from the request",
                    obj.getJsonNumber("id").longValue(), 10001);
        }
        return obj;
    }

    @Path("structure")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public JsonStructure object(JsonStructure struct) {
        JsonObject obj = (JsonObject) struct;
        Assert.assertTrue("The field 'name' didn't propagated correctly from the request", obj.containsKey("name"));
        Assert.assertEquals("The value of field 'name' didn't propagated correctly from the request",
                obj.getJsonString("name").getString(), "Bill");
        return obj;
    }

    @Path("number")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonNumber testNumber(JsonNumber number) {
        return Json.createValue(number.intValue() + 100);
    }

    @Path("string")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonString testString(JsonString string) {
        return Json.createValue("Hello " + string.getString());
    }

}
