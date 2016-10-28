package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Set;

@Path("/set")
public interface HeaderParamsAsPrimitivesSetProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("header") Set<String> v);
}
