package org.jboss.resteasy.test.cdi.inheritence.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

@Alternative
@Stereotype
@Target(TYPE)
@Retention(RUNTIME)
public @interface CDIInheritenceStereotypeAlternative {
}
