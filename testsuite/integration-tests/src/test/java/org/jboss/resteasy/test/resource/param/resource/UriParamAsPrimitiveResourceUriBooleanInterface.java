package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/boolean/{arg}")
public interface UriParamAsPrimitiveResourceUriBooleanInterface {
   @GET
   String doGet(@PathParam("arg") boolean v);
}
