package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ProduceStereotype;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/stereotype")
public class DifferentStereotypeResource {

    @GET
    @Path("/produces")
    @ProduceStereotype
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response produces()
    {
        return Response.ok("{}").build();
    }
}
