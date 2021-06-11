package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.NoAnnotationStereotype;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/stereotype")
@NoAnnotationStereotype
public class NoAnnotationResource {

    @GET
    @Path("/produces")
    public Response produces()
    {
        return Response.ok("{}").build();
    }

}
