package org.jboss.resteasy.cdi.interceptors;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 14, 2012
 */
@InterceptorBinding
@Target({TYPE, METHOD})
@Retention(value = RUNTIME)
public @interface ResponseFilterInterceptorBinding
{
}
