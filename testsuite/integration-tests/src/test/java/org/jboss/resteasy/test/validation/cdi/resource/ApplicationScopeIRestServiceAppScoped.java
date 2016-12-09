package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/testapp")
public interface ApplicationScopeIRestServiceAppScoped {
   
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @NotNull
    String sendDto(@NotNull @Valid ApplicationScopeMyDto myDto);
}
