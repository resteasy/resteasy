package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;

public interface AnnotationInheritanceGenericsInterface<ENTITY_TYPE, ID_TYPE> {

    @GET
    Collection<ENTITY_TYPE> get();

    @GET
    @Path("{id}")
    ENTITY_TYPE get(@PathParam("id") ID_TYPE id);

    @POST
    ENTITY_TYPE post(ENTITY_TYPE entity);

    @PUT
    @Path("{id}")
    ENTITY_TYPE put(@PathParam("id") ID_TYPE id, ENTITY_TYPE entity);

}
