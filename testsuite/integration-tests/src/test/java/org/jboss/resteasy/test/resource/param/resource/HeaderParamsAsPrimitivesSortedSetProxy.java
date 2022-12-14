package org.jboss.resteasy.test.resource.param.resource;

import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/sortedset")
public interface HeaderParamsAsPrimitivesSortedSetProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("header") SortedSet<String> v);
}
