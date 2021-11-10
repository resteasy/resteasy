package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface GenericSuperInterfaceDataCenterResource extends GenericSuperInterfaceUpdatableResource<GenericSuperInterfaceDataCenter> {

   @Path("permissions")
   GenericSuperInterfaceAssignedPermissionsResource getPermissionsResource();

}
