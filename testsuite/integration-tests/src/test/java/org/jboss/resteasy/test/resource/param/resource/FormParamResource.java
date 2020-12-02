package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class FormParamResource {
   @POST
   @Path("form")
   @Consumes("application/x-www-form-urlencoded")
   public String post(@Encoded @FormParam("param") String param) {
      return param;
   }
}
