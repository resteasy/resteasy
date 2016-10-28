package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Target(TYPE)
@Retention(value = RUNTIME)
public @interface InterceptorLifecycleBinding {
}
