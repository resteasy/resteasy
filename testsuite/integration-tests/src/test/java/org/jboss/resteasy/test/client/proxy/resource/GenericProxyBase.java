package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public interface GenericProxyBase<T> {
    @POST
    @Path("/hello")
    @Produces("text/plain")
    @Consumes("text/plain")
    String sayHi(T in);
}
