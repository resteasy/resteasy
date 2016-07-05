package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("resource")
public class ExceptionMapperMarshalResource {
    @GET
    @Path("custom")
    public List<ExceptionMapperMarshalName> custom() throws Throwable {
        throw new ExceptionMapperMarshalMyCustomException("hello");
    }


}
