package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

@Path("/map")
public class MapResource {
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    public Map<String, MapFoo> post(Map<String, MapFoo> map) {
        Assert.assertEquals("The map from the request has unexpected content", 2, map.size());
        Assert.assertNotNull("The map from the request has unexpected content", map.get("bill"));
        Assert.assertNotNull("The map from the request has unexpected content", map.get("monica"));
        Assert.assertEquals("The map from the request has unexpected content", map.get("bill").getName(), "bill");
        Assert.assertEquals("The map from the request has unexpected content", map.get("monica").getName(), "monica");
        return map;
    }

    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("/wrapped")
    @WrappedMap(namespace = "")
    public Map<String, MapFoo> postWrapped(@WrappedMap(namespace = "") Map<String, MapFoo> map) {
        Assert.assertEquals("The map from the request has unexpected content", 2, map.size());
        Assert.assertNotNull("The map from the request has unexpected content", map.get("bill"));
        Assert.assertNotNull("The map from the request has unexpected content", map.get("monica"));
        Assert.assertEquals("The map from the request has unexpected content", map.get("bill").getName(), "bill");
        Assert.assertEquals("The map from the request has unexpected content", map.get("monica").getName(), "monica");
        return map;
    }

    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("/integerFoo")
    public Map<Integer, MapFoo> postIntegerFoo(Map<String, MapFoo> map) {
        Assert.assertEquals("The map from the request has unexpected content", 2, map.size());
        Assert.assertNotNull("The map from the request has unexpected content", map.get("1"));
        Assert.assertNotNull("The map from the request has unexpected content", map.get("2"));
        Assert.assertEquals("The map from the request has unexpected content", map.get("1").getName(), "bill");
        Assert.assertEquals("The map from the request has unexpected content", map.get("2").getName(), "monica");
        Map<Integer, MapFoo> result = new HashMap<Integer, MapFoo>();
        for (Map.Entry<String, MapFoo> e : map.entrySet()) {
            result.put(Integer.valueOf(e.getKey()), e.getValue());
        }
        return result;
    }
}
