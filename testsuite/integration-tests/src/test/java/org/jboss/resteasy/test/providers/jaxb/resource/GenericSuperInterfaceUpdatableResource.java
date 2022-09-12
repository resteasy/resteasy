package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces({MediaType.APPLICATION_XML})
public interface GenericSuperInterfaceUpdatableResource<R extends GenericSuperInterfaceBaseResource> {

   @GET
   R get();

   @PUT
   @Consumes({MediaType.APPLICATION_XML})
   R update(R resource);
}
