package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/testapp")
public interface ApplicationScopeIRestServiceAppScoped {

   @POST
   @Path("/send")
   @Consumes(MediaType.APPLICATION_JSON)
   @NotNull
   String sendDto(@NotNull @Valid ApplicationScopeMyDto myDto);
}
