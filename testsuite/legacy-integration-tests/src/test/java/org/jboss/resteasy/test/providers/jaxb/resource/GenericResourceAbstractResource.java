package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class GenericResourceAbstractResource<T> {

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createEntity(T entity) {
        return Response.ok("Success!").build();
    }

}
