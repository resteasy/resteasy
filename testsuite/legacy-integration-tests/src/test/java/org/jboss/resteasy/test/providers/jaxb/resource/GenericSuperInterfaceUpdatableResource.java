package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces({MediaType.APPLICATION_XML})
public interface GenericSuperInterfaceUpdatableResource<R extends GenericSuperInterfaceBaseResource> {

    @GET
    R get();

    @PUT
    @Consumes({MediaType.APPLICATION_XML})
    R update(R resource);
}
