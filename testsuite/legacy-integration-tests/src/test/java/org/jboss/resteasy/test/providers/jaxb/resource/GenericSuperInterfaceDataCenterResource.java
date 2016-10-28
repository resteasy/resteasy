package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface GenericSuperInterfaceDataCenterResource extends GenericSuperInterfaceUpdatableResource<GenericSuperInterfaceDataCenter> {

    @Path("permissions")
    GenericSuperInterfaceAssignedPermissionsResource getPermissionsResource();

}
