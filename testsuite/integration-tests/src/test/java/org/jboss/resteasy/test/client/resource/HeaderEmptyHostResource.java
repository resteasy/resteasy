package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/headeremptyhostresource")
public class HeaderEmptyHostResource {
    private static Logger logger = Logger.getLogger(HeaderEmptyHostResource.class);

    @Context
    UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    public String getUriInfo() {
        logger.info("uriInfo = " + uriInfo.getRequestUri());
        return "uriInfo: " + uriInfo.getRequestUri().toString();
    }

}
