package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.EncodedStereotype;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
@EncodedStereotype
public class EncodedStereotypeResource {

    @GET
    @Path("{param}")
    public Response get(@PathParam("param") String message){
        return Response.ok(message).build();
    }

}
