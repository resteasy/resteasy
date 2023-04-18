package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/test")
public interface UnauthorizedExceptionInterface {
    @POST
    @Consumes("text/plain")
    void postIt(String msg);
}
