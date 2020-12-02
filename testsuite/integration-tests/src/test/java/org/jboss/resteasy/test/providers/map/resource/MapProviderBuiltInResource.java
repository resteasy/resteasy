package org.jboss.resteasy.test.providers.map.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedMap;

@Path("/")
public class MapProviderBuiltInResource {
   @Path("map")
   @POST
   public MultivaluedMap<String, String> map(MultivaluedMap<String, String> map) {
      return map;
   }
}
