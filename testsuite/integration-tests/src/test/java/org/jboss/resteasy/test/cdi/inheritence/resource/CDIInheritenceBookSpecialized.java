package org.jboss.resteasy.test.cdi.inheritence.resource;

import jakarta.enterprise.inject.Specializes;

/**
 * If this class is enabled, it will be injected into the Book injection point in InheritanceResource.
 */
@Specializes
@CDIInheritenceStereotypeAlternative
public class CDIInheritenceBookSpecialized extends CDIInheritenceBook {
}
