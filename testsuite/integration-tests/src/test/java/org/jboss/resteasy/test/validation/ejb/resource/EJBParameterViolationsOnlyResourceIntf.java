package org.jboss.resteasy.test.validation.ejb.resource;

import javax.ejb.Remote;
import javax.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Remote
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public interface EJBParameterViolationsOnlyResourceIntf
{
   @POST
   @Path("validation")
   String testValidation(@Valid EJBParameterViolationsOnlyDataObject payload);

   @GET
   @Path("used")
   boolean used();

   @GET
   @Path("reset")
   void reset();
}
