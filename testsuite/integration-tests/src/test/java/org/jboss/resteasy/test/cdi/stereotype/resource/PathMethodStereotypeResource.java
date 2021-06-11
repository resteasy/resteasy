package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.PathStereotype;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/stereotype")
public class PathMethodStereotypeResource {

    @GET
    @Path("/produces")
    @PathStereotype
    @Produces(MediaType.APPLICATION_JSON)
    public Response produces()
    {
        return Response.ok("{}").build();
    }

}