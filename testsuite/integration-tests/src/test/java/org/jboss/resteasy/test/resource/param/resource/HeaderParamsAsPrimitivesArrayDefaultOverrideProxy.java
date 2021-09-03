package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array/default/override")
public interface HeaderParamsAsPrimitivesArrayDefaultOverrideProxy {
   @GET
   @Produces("application/boolean")
   String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") boolean[] v);

   @GET
   @Produces("application/short")
   String doGetShort(@HeaderParam("int") @DefaultValue("0") short[] v);
}
