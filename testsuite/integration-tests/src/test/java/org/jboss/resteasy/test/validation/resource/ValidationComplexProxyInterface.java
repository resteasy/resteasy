package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("proxy")
public interface ValidationComplexProxyInterface {
   @GET
   @Produces("text/plain")
   @Size(min = 2, max = 4)
   String g();

   @POST
   @Path("{s}")
   void s(@PathParam("s") String s);
}
