package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/array")
public interface HeaderParamsAsPrimitivesArrayProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("boolean") boolean[] v);

    @GET
    @Produces("application/short")
    String doGetShort(@HeaderParam("short") short[] v);
}
