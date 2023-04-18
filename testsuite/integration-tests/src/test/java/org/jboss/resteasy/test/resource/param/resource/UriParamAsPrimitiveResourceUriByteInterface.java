package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/byte/{arg}")
public interface UriParamAsPrimitiveResourceUriByteInterface {
    @GET
    String doGet(@PathParam("arg") byte v);
}
