package org.jboss.resteasy.test.injection.resource;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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