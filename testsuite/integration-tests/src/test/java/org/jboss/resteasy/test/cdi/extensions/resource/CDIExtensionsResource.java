package org.jboss.resteasy.test.cdi.extensions.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.cdi.util.Utilities;

@Path("/extension")
@RequestScoped
public class CDIExtensionsResource {
    private static Logger log = Logger.getLogger(CDIExtensionsResource.class);

    @Inject
    @CDIExtensionsBoston
    CDIExtensionsBostonHolder holder;

    @POST
    @Path("boston")
    public Response setup() {
        log.info("Injected object for response (holder): " + holder);
        boolean response = true;
        response &= Utilities.isBoston(holder.getClass());
        response &= holder.getLeaf() != null;
        response &= holder.getReader() != null;
        return response ? Response.ok().build() : Response.serverError().build();
    }

}
