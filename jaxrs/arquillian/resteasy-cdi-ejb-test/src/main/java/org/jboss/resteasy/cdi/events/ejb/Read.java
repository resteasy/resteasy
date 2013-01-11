package org.jboss.resteasy.cdi.events.ejb;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 25, 2012
 */
@Qualifier
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Read
{
   String context();
}

