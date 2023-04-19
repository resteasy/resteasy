package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/boolean/{arg}")
public interface UriParamAsPrimitiveResourceUriBooleanInterface {
    @GET
    String doGet(@PathParam("arg") boolean v);
}
