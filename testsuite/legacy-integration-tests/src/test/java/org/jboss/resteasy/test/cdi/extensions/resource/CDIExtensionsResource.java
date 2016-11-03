package org.jboss.resteasy.test.cdi.extensions.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.cdi.util.Utilities;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
