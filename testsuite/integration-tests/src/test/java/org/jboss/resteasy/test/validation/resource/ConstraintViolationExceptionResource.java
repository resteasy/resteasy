package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/echo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConstraintViolationExceptionResource {

    @GET
    @Path("test/{p}")
    Response validate(@Size(min=3) @PathParam("p") String p);
}
