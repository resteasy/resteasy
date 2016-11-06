package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("resource")
public class ExceptionMapperResource {
    @GET
    @Path("responseok")
    public String responseOk() {
        Response r = Response.ok("hello").build();
        throw new WebApplicationException(r);
    }

    @GET
    @Path("custom")
    public String custom() throws Throwable {
        throw new ExceptionMapperMyCustomException("hello");
    }
    
    @GET
    @Path("sub")
    public String sub() throws Throwable {
        throw new ExceptionMapperMyCustomSubException("sub");
    }
}
