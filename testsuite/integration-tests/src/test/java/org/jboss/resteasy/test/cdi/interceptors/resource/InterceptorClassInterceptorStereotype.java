package org.jboss.resteasy.test.cdi.interceptors.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.inject.Stereotype;

@InterceptorClassBinding
@Stereotype
@Target(TYPE)
@Retention(RUNTIME)
public @interface InterceptorClassInterceptorStereotype {
}
