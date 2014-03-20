package org.jboss.resteasy.plugins.validation.cdi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 7, 2014
 */
@Inherited
@InterceptorBinding
@Target({TYPE})
@Retention(RUNTIME)
public @interface ResteasyValidationCdiEnabledAnnotation
{
}
