package org.jboss.resteasy.test.core.smoke.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/")
public class WireSmokeLocatingResource {
    private static Logger logger = Logger.getLogger(WireSmokeLocatingResource.class);
    @Inject
    private WireSmokeSimpleResource wireSmokeSimpleResource;
    @Inject
    private WireSmokeSimpleSubresource wireSmokeSimpleSubresource;

    @Path("locating")
    public WireSmokeSimpleResource getLocating() {
        logger.info("LOCATING...");
        return wireSmokeSimpleResource;
    }

    @Path("subresource")
    public WireSmokeSimpleSubresource getSubresource() {
        logger.info("Subresource");
        return wireSmokeSimpleSubresource;
    }

    @Path("notlocating")
    public WireSmokeSimpleResource getNotLocating() {
        logger.info("NOT LOCATING... i.e. returning null");
        return null;
    }

}
