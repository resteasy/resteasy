package org.jboss.resteasy.test.providers.jettison.resource;

import org.junit.Assert;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import java.util.HashMap;
import java.util.Map;

@Path("/map")
public class JsonMapResource {
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Map<String, JsonMapFoo> post(Map<String, JsonMapFoo> map) {
        Assert.assertEquals("The map doesn't have the correct size", 2, map.size());
        Assert.assertNotNull("The map doesn't contain the value", map.get("bill"));
        Assert.assertNotNull("The map doesn't contain the value", map.get("monica"));
        Assert.assertEquals("The map doesn't contain the correct value", map.get("bill").getName(), "bill");
        Assert.assertEquals("The map doesn't contain the correct value", map.get("monica").getName(), "monica");
        return map;
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("empty")
    public Map<String, JsonMapFoo> postEmpty(Map<String, JsonMapFoo> map) {
        Assert.assertEquals("The map was expected to be empty", 0, map.size());
        return map;
    }

    @GET
    @Produces("application/json")
    public Map<String, JsonMapFoo> get() {
        HashMap<String, JsonMapFoo> map = new HashMap<String, JsonMapFoo>();
        map.put("bill", new JsonMapFoo("bill"));
        map.put("monica", new JsonMapFoo("monica"));
        return map;
    }
}
