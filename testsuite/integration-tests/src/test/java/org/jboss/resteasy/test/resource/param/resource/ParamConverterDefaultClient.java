package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/")
public interface ParamConverterDefaultClient {
    @PUT
    void put();
}
