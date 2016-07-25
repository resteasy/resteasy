package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Path;

@Path("/")
public class GenericSuperInterfaceTop {
    @Path("datacenters")
    public GenericSuperInterfaceBackendDataCentersResource getDatacenters() {
        return new GenericSuperInterfaceBackendDataCentersResource();
    }

    // here we get GenericSuperInterfaceBackendDataCentersResource collection sub-resource GenericSuperInterfaceBackendDataCenterResource
    // e.g "/datacenters/xxx"

    // and invoke one of the methods inherited from GenericSuperInterfaceUpdatableResource on it
    // e.g get()/update()
}
