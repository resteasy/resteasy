package org.jboss.resteasy.test.resource.param.resource;

import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/set")
public interface HeaderParamsAsPrimitivesSetProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("header") Set<String> v);
}
