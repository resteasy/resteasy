package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ConsumeStereotype;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ProduceStereotype;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/stereotype")
@ConsumeStereotype
@ProduceStereotype
public class TwoStereotypesResource {
    @GET
    @Path("/produces")
    public Response produces()
    {
        return Response.ok("{}").build();
    }

    @POST
    @Path("/consumes")
    public Response consumes(String data)
    {
        return Response.ok(data).build();
    }
}
