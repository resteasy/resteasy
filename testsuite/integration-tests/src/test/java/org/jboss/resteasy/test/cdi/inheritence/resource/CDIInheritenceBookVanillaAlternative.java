package org.jboss.resteasy.test.cdi.inheritence.resource;

/**
 * This class is never used, because the Book injection point in InheritanceResource uses the
 *
 * @SelectBook qualifier.  Even if this alternative is enabled, a Book will be injected.
 */
@CDIInheritenceStereotypeAlternative
public class CDIInheritenceBookVanillaAlternative extends CDIInheritenceBook {
}
