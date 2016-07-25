package org.jboss.resteasy.test.cdi.inheritence.resource;

/**
 * If this class is enabled, it will be injected into the Book injection point in InheritanceResource.
 */
@CDIInheritenceStereotypeAlternative
@CDIInheritenceSelectBook
public class CDIInheritenceBookSelectedAlternative extends CDIInheritenceBook {
}
