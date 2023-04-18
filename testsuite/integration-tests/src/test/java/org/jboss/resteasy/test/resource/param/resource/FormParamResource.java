package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class FormParamResource {
    @POST
    @Path("form")
    @Consumes("application/x-www-form-urlencoded")
    public String post(@Encoded @FormParam("param") String param) {
        return param;
    }
}
