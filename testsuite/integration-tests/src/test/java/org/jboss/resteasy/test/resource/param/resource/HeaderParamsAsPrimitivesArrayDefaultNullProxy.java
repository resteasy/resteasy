package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array/default/null")
public interface HeaderParamsAsPrimitivesArrayDefaultNullProxy {
   @GET
   @Produces("application/boolean")
   String doGetBoolean();

   @GET
   @Produces("application/short")
   String doGetShort();
}
