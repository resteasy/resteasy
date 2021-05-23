package org.jboss.resteasy.test.cdi.inheritence.resource;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Stereotype;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Alternative
@Stereotype
@Target(TYPE)
@Retention(RUNTIME)
public @interface CDIInheritenceStereotypeAlternative {
}
