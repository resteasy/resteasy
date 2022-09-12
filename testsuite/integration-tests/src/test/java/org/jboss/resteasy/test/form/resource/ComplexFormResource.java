package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class ComplexFormResource {
   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Path("/person")
   public String post(@Form ComplexFormPerson p) {
      return p.toString();
   }
}
