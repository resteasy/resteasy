package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class ExceptionHandlingResource {
    @Path("test")
    @POST
    public void post() throws Exception {
        throw new Exception("test");
    }
}
