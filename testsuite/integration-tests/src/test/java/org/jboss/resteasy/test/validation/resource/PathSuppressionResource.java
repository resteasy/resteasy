package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("all")
@PathSuppressionClassConstraint(5)
public class PathSuppressionResource {
   @Size(min = 2, max = 4)
   @PathParam("s")
   String s;

   private String t;

   @Size(min = 3, max = 5)
   public String getT() {
      return t;
   }

   public String retrieveS() {
      return s;
   }

   @PathParam("t")
   public void setT(String t) {
      this.t = t;
   }

   @GET
   @Path("{s}/{t}/{u}")
   @Size(max = 3)
   @Produces(MediaType.TEXT_PLAIN)
   public String test(@Size(min = 4, max = 6) @PathParam("u") String u) {
      return s + t + u;
   }
}
