package org.jboss.resteasy.test.cdi.ejb.resource;

import javax.ejb.Stateless;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("stateless")
@Stateless
public class EJBCDIValidationStatelessResource {

   @Size(min=3)
   @PathParam("name")
   private String name;

   public String getName() {
      return name;
   }

   @POST
   @Path("/post/{name}")
   @Consumes(MediaType.APPLICATION_JSON)
   public void post(@Min(1) long id, String from, String to) {
      // nothing to do
   }

   @GET
   @Path("set/{name}")
   public void get() {
   }
}