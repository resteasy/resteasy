package org.jboss.resteasy.test.cdi.injection.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ApplicationScoped
@Stereotype
@Target(TYPE)
@Retention(RUNTIME)
public @interface CDIInjectionScopeStereotype {
}
