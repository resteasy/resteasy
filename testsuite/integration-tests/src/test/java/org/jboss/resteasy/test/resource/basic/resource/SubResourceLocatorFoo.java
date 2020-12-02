package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;

public interface SubResourceLocatorFoo<T> {
   @GET
   T getFoo(@HeaderParam("foo") String val);
}
