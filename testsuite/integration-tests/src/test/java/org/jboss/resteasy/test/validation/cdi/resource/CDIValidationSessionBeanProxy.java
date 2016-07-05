package org.jboss.resteasy.test.validation.cdi.resource;

import javax.ejb.Local;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Local
@Path("test")
public interface CDIValidationSessionBeanProxy {
    @GET
    @Path("resource/{param}")
    int test(@Min(7) @PathParam("param") int param);
}