package org.jboss.resteasy.test.cdi.modules.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

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
