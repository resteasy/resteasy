package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}")
public class ValidationComplexResourceWithProperty {
   private String s;

   @POST
   @Path("{unused}")
   public void post() {
   }

   @Size(min = 2, max = 4)
   public String getS() {
      return s;
   }

   @PathParam("s")
   public void setS(String s) {
      this.s = s;
   }
}
