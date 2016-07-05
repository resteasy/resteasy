package org.jboss.resteasy.test.providers.jaxb.resource;

public class GenericSuperInterfaceBackendDataCenterResource
        extends GenericSuperInterfaceAbstractBackendSubResource<GenericSuperInterfaceDataCenter, GenericSuperInterfaceStoragePool>
        implements GenericSuperInterfaceDataCenterResource {
    public GenericSuperInterfaceBackendDataCenterResource(final String id,
               final GenericSuperInterfaceBackendDataCentersResource backendDataCentersResource) {

    }

    @Override
    public GenericSuperInterfaceAssignedPermissionsResource getPermissionsResource() {
        return null;
    }

    @Override
    public GenericSuperInterfaceDataCenter get() {
        GenericSuperInterfaceDataCenter dc = new GenericSuperInterfaceDataCenter();
        dc.setName("Bill");
        return dc;
    }

    @Override
    public GenericSuperInterfaceDataCenter update(GenericSuperInterfaceDataCenter resource) {
        return resource;
    }

}
