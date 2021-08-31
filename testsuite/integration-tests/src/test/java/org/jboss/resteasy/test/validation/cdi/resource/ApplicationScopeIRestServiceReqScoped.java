package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/testreq")
public interface ApplicationScopeIRestServiceReqScoped {

   @POST
   @Path("/send")
   @Consumes(MediaType.APPLICATION_JSON)
   Response sendDto(@NotNull @Valid ApplicationScopeMyDto myDto);
}
