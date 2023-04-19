package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("")
public class ValidationHibernateI18NResource {
    @GET
    @Path("test")
    @Size(min = 2)
    public String test() {
        return "a";
    }
}
