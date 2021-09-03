package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface GenericProxyBase<T> {
   @POST
   @Path("/hello")
   @Produces("text/plain")
   @Consumes("text/plain")
   String sayHi(T in);
}
