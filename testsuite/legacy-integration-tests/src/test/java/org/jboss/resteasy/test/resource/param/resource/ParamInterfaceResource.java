package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public interface ParamInterfaceResource {
    @Path("matrix")
    @GET
    @Produces("text/plain")
    String getMatrix(@MatrixParam("param") String matrix);


    @Path("cookie")
    @GET
    @Produces("text/plain")
    String getCookie(@CookieParam("param") String cookie);

    @Path("header")
    @GET
    @Produces("text/plain")
    String getHeader(@HeaderParam("custom") String header);
}
