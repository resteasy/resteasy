package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NameBinding
@Target({TYPE, METHOD})
@Retention(value = RUNTIME)
public @interface InterceptorFilterBinding {
}
