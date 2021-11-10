package org.jboss.resteasy.test.injection.resource;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/normal")
public class PostConstructInjectionResource {

   @Size(max=3)
   private String s = "ab";

   @Path("get")
   @GET
   public String get() {
      return s;
   }

   @PostConstruct
   public void postConstruct() {
      s = "abcd";
   }
}