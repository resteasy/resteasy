package org.jboss.resteasy.test.cdi.modules.resource;

import java.util.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Stateless
@Path("modules")
public class CDIModulesModulesResource implements CDIModulesModulesResourceIntf {
    @Inject
    private Logger log;

    @Inject
    @CDIModulesInjectableBinder
    private CDIModulesInjectableIntf injectable;

    @Override
    @GET
    @Path("test")
    public Response test() {
        log.info("entering CDIValidationCoreResource.test()");
        return (injectable != null) ? Response.ok().build() : Response.serverError().build();
    }
}
