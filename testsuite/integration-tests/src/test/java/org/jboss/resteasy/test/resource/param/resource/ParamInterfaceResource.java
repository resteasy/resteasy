package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

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
