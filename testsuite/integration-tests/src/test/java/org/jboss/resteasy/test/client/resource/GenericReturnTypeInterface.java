package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

public interface GenericReturnTypeInterface<T> {
   @GET
   @Path("t")
   T t();
}
