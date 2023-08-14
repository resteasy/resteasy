package org.jboss.resteasy.test.client.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("/headeremptyhostresource")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class HeaderEmptyHostResource {
    private static Logger logger = Logger.getLogger(HeaderEmptyHostResource.class);

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    public String getUriInfo() {
        logger.info("uriInfo = " + uriInfo.getRequestUri());
        return "uriInfo: " + uriInfo.getRequestUri().toString();
    }

}
