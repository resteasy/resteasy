package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

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
