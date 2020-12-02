package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("")
public class ValidationNullErrorValueResourceWithNullFieldAndProperty {
   @NotNull
   private String s;

   @Path("get")
   @GET
   public void doGet() {
   }

   @NotNull
   public String getT() {
      return null;
   }

   public void setS(String s) {
      this.s = s;
   }
}
