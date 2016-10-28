package org.jboss.resteasy.test.providers.map.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

@Path("/")
public class MapProviderResource {
    @Path("map")
    @POST
    public MultivaluedMap<String, String> map(MultivaluedMap<String, String> map) {
        return map;
    }
}
