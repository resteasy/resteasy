package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("")
public class ValidationNullErrorValueResourceWithNullParameterAndReturnValue {
   @Path("post")
   @POST
   public void doPost(@NotNull @QueryParam("q") String q) {
   }

   @Path("get")
   @GET
   @NotNull
   public String doGet() {
      return null;
   }
}
