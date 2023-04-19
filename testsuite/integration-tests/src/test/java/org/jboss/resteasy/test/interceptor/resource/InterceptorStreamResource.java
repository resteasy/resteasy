package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class InterceptorStreamResource {
    @POST
    @Path("test")
    public String createBook(String test) {
        return test;
    }
}
