package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/")
public interface ParamConverterDefaultClient {
   @PUT
   void put();
}
