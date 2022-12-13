package org.jboss.resteasy.test.providers.jaxb.resource;

import java.io.Serializable;

public interface GenericSuperInterfaceBusinessEntity<T extends Serializable> extends Serializable {

    /**
     * Returns the unique ID of the business entity.
     *
     * @return The unique ID of the business entity.
     */
    T getId();

    /**
     * Sets the unique ID of the business entity
     *
     * @param id The unique ID of the business entity.
     */
    void setId(T id);
}
