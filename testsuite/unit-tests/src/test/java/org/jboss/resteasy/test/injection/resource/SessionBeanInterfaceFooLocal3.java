package org.jboss.resteasy.test.injection.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("foo")
public interface SessionBeanInterfaceFooLocal3 {
   @GET
   String foo3();
}
