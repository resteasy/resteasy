package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;
import org.junit.jupiter.api.Assertions;

@Path("/map")
public class MapResource {
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    public Map<String, MapFoo> post(Map<String, MapFoo> map) {
        Assertions.assertEquals(2, map.size(), "The map from the request has unexpected content");
        Assertions.assertNotNull(map.get("bill"), "The map from the request has unexpected content");
        Assertions.assertNotNull(map.get("monica"), "The map from the request has unexpected content");
        Assertions.assertEquals(map.get("bill").getName(), "bill",
                "The map from the request has unexpected content");
        Assertions.assertEquals(map.get("monica").getName(), "monica",
                "The map from the request has unexpected content");
        return map;
    }

    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("/wrapped")
    @WrappedMap(namespace = "")
    public Map<String, MapFoo> postWrapped(@WrappedMap(namespace = "") Map<String, MapFoo> map) {
        Assertions.assertEquals(2, map.size(), "The map from the request has unexpected content");
        Assertions.assertNotNull(map.get("bill"), "The map from the request has unexpected content");
        Assertions.assertNotNull(map.get("monica"), "The map from the request has unexpected content");
        Assertions.assertEquals(map.get("bill").getName(), "bill",
                "The map from the request has unexpected content");
        Assertions.assertEquals(map.get("monica").getName(), "monica",
                "The map from the request has unexpected content");
        return map;
    }

    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("/integerFoo")
    public Map<Integer, MapFoo> postIntegerFoo(Map<String, MapFoo> map) {
        Assertions.assertEquals(2, map.size(), "The map from the request has unexpected content");
        Assertions.assertNotNull(map.get("1"), "The map from the request has unexpected content");
        Assertions.assertNotNull(map.get("2"), "The map from the request has unexpected content");
        Assertions.assertEquals(map.get("1").getName(), "bill",
                "The map from the request has unexpected content");
        Assertions.assertEquals(map.get("2").getName(), "monica",
                "The map from the request has unexpected content");
        Map<Integer, MapFoo> result = new HashMap<Integer, MapFoo>();
        for (Map.Entry<String, MapFoo> e : map.entrySet()) {
            result.put(Integer.valueOf(e.getKey()), e.getValue());
        }
        return result;
    }
}
