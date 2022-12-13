package org.jboss.resteasy.test.cdi.inheritence.resource;

/**
 * An instance of this class will be injected into the Book injection point in InheritenceResource
 * if no alternatives are enabled.
 */
@CDIInheritenceSelectBook
public class CDIInheritenceBook {
    public Class<?> getType() {
        return this.getClass();
    }
}
