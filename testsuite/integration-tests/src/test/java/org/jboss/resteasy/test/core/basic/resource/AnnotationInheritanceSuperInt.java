package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public interface AnnotationInheritanceSuperInt {
   @Path("foo")
   @GET
   @Produces("application/json")
   String getFoo();
}
