package org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes;

import javax.enterprise.inject.Stereotype;
import javax.ws.rs.Path;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Stereotype
@Path("/stereotype")
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathStereotype{
}