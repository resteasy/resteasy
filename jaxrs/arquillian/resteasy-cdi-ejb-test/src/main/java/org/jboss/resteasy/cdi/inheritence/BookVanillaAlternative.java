package org.jboss.resteasy.cdi.inheritence;

/**
 * This class is never used, because the Book injection point in InheritanceResource uses the
 * @SelectBook qualifier.  Even if this alternative is enabled, a Book will be injected.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 5, 2012
 */
@StereotypeAlternative
public class BookVanillaAlternative extends Book
{
}
