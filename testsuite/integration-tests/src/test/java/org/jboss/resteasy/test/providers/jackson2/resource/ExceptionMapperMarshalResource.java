package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("resource")
public class ExceptionMapperMarshalResource {
    @GET
    @Path("custom")
    public List<ExceptionMapperMarshalName> custom() throws Throwable {
        throw new ExceptionMapperMarshalMyCustomException("hello");
    }

    @GET
    @Path("customME")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        MyEntity entity = new MyEntity();
        return Response.status(Response.Status.OK).entity(entity).build();
    }
}
