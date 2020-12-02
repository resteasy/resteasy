package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface AnnotationInheritanceSuperInt {
   @Path("foo")
   @GET
   @Produces("application/json")
   String getFoo();
}
