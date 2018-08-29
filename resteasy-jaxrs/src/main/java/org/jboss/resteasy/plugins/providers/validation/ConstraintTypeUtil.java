package org.jboss.resteasy.plugins.providers.validation;

import org.jboss.resteasy.api.validation.ConstraintType;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 25, 2013
 */
public interface ConstraintTypeUtil
{
   ConstraintType.Type getConstraintType(Object o);
}
