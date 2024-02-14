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

import org.junit.jupiter.api.Assertions;

@Path("/test/json")
public class JsonpResource {
    @Path("array")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public JsonArray array(JsonArray array) {
        Assertions.assertEquals(2, array.size(), "The request didn't contain 2 json elements");
        JsonObject obj = array.getJsonObject(0);
        Assertions.assertTrue(obj.containsKey("name"),
                "The field 'name' didn't propagated correctly from the request for object[0]");
        Assertions.assertEquals(obj.getJsonString("name").getString(), "Bill",
                "The value of field 'name' didn't propagated correctly from the request for object[0]");
        obj = array.getJsonObject(1);
        Assertions.assertTrue(obj.containsKey("name"),
                "The field 'name' didn't propagated correctly from the request for object[1]");
        Assertions.assertEquals(obj.getJsonString("name").getString(), "Monica",
                "The value of field 'name' didn't propagated correctly from the request for object[1]");
        return array;
    }

    @Path("object")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public JsonObject object(JsonObject obj) {
        Assertions.assertTrue(obj.containsKey("name"), "The field 'name' didn't propagated correctly from the request");
        Assertions.assertEquals(obj.getJsonString("name").getString(), "Bill",
                "The value of field 'name' didn't propagated correctly from the request");
        if (obj.containsKey("id")) {
            Assertions.assertEquals(obj.getJsonNumber("id").longValue(), 10001,
                    "The value of field 'id' didn't propagated correctly from the request");
        }
        return obj;
    }

    @Path("structure")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public JsonStructure object(JsonStructure struct) {
        JsonObject obj = (JsonObject) struct;
        Assertions.assertTrue(obj.containsKey("name"),
                "The field 'name' didn't propagated correctly from the request");
        Assertions.assertEquals(obj.getJsonString("name").getString(), "Bill",
                "The value of field 'name' didn't propagated correctly from the request");
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
