package org.jboss.resteasy.test.providers.jaxb.resource;

public class GenericSuperInterfaceStoragePool extends GenericSuperInterfaceIVdcQueryable implements GenericSuperInterfaceINotifyPropertyChanged, GenericSuperInterfaceBusinessEntity<GenericSuperInterfaceGuid> {

    @Override
    public GenericSuperInterfaceGuid getId() {
        return null;
    }

    @Override
    public void setId(GenericSuperInterfaceGuid id) {
    }
}
