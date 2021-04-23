package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class EmptyArrayValidationResource {

   @POST
   @Path("emptyarray")
   @Consumes(MediaType.APPLICATION_JSON)
   public void test(@Valid EmptyArrayValidationFoo foo) {
   }
}
