package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/byte/{arg}")
public interface UriParamAsPrimitiveResourceUriByteInterface {
   @GET
   String doGet(@PathParam("arg") byte v);
}
