package org.jboss.resteasy.test.providers.jsonp.resource;

import org.junit.Assert;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
        Assert.assertEquals("The value of field 'name' didn't propagated correctly from the request"
                , obj.getJsonString("name").getString(), "Bill");
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
}
