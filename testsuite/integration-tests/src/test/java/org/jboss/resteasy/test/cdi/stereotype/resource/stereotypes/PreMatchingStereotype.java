package org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes;

import javax.enterprise.inject.Stereotype;
import javax.ws.rs.container.PreMatching;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Stereotype
@PreMatching
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreMatchingStereotype {
}
