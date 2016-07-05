package org.jboss.resteasy.test.core.smoke.resource;


import org.jboss.logging.Logger;

import javax.ws.rs.Path;

@Path("/")
public class WireSmokeLocatingResource {
    private static Logger logger = Logger.getLogger(WireSmokeLocatingResource.class);

    @Path("locating")
    public WireSmokeSimpleResource getLocating() {
        logger.info("LOCATING...");
        return new WireSmokeSimpleResource();
    }


    @Path("subresource")
    public WireSmokeSimpleSubresource getSubresource() {
        logger.info("Subresource");
        return new WireSmokeSimpleSubresource();
    }


    @Path("notlocating")
    public WireSmokeSimpleResource getNotLocating() {
        logger.info("NOT LOCATING... i.e. returning null");
        return null;
    }

}
