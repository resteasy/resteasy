package org.jboss.resteasy.test.security.resource;

import org.jboss.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Path("/secured2")
public class BasicAuthBaseResourceMoreSecured {
    private static Logger logger = Logger.getLogger(BasicAuthBaseResourceMoreSecured.class);

    public String get(@Context SecurityContext ctx) {
        logger.info("********* IN SECURE CLIENT");
        if (!ctx.isUserInRole("admin")) {
            logger.info("NOT IN ROLE!!!!");
            throw new WebApplicationException(403);
        }
        return "hello";
    }

    @GET
    @Path("/authorized")
    @RolesAllowed("admin")
    public String getAuthorized() {
        return "authorized";
    }

}
