package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/array/default/override")
public interface HeaderParamsAsPrimitivesArrayDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") boolean[] v);

    @GET
    @Produces("application/short")
    String doGetShort(@HeaderParam("int") @DefaultValue("0") short[] v);
}
