package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/array/default")
public interface HeaderParamsAsPrimitivesArrayDefaultProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean();

    @GET
    @Produces("application/short")
    String doGetShort();
}
