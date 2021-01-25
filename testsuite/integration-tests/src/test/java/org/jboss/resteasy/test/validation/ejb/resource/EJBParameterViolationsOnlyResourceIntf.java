package org.jboss.resteasy.test.validation.ejb.resource;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
