package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/testreq")
public interface ApplicationScopeIRestServiceReqScoped {
   
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    Response sendDto(@NotNull @Valid ApplicationScopeMyDto myDto);
}
