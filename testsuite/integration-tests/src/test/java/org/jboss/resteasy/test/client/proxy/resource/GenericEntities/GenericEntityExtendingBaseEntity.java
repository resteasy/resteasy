package org.jboss.resteasy.test.client.proxy.resource.GenericEntities;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public interface GenericEntityExtendingBaseEntity<T extends BaseEntity> {

    @GET
    @Path("one")
    @Produces(MediaType.APPLICATION_JSON)
    T findOne();

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    List<T> findAll();

}
