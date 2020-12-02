package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array")
public interface HeaderParamsAsPrimitivesArrayProxy {
   @GET
   @Produces("application/boolean")
   String doGetBoolean(@HeaderParam("boolean") boolean[] v);

   @GET
   @Produces("application/short")
   String doGetShort(@HeaderParam("short") short[] v);
}
