package org.jboss.resteasy.test.xxe.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class ExternalParameterEntityResource {
   @POST
   @Path("test")
   @Consumes(MediaType.APPLICATION_XML)
   public String post(ExternalParameterEntityWrapper wrapper) {
      return wrapper.getName();
   }
}
