package org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes;

import javax.enterprise.inject.Stereotype;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Stereotype
@Consumes(MediaType.APPLICATION_XML)
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsumeStereotype {
}
