package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.core.Response;

public class GenericSuperInterfaceBackendDataCentersResource extends
        GenericSuperInterfaceAbstractBackendCollectionResource<GenericSuperInterfaceDataCenter, GenericSuperInterfaceStoragePool> implements GenericSuperInterfaceDataCentersResource {

    @Override
    public GenericSuperInterfaceDataCenters list() {
        return null;
    }

    @Override
    public Response add(GenericSuperInterfaceDataCenter dataCenter) {
        return null;
    }

    @Override
    public Response remove(String id, GenericSuperInterfaceAction action) {
        return null;
    }

    @Override
    public GenericSuperInterfaceDataCenterResource getDataCenterSubResource(String id) {
        return new GenericSuperInterfaceBackendDataCenterResource(id, this);
    }

    @Override
    public Response remove(String id) {
        return null;
    }
}
