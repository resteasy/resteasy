package org.jboss.resteasy.cdi.test.interceptor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;

import javax.interceptor.InterceptorBinding;


@InterceptorBinding
@Target( { TYPE, METHOD } )
@Retention(RUNTIME)
@Inherited
public @interface TestInterceptorBinding
{
}
