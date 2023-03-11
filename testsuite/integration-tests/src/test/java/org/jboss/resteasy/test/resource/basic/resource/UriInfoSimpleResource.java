package org.jboss.resteasy.test.resource.basic.resource;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.junit.Assert;

@Path("/")
public class UriInfoSimpleResource {
    private static Logger logger = Logger.getLogger(UriInfoSimpleResource.class);

    @Context
    UriInfo myInfo;

    @Path("/simple")
    @GET
    public String get(@Context UriInfo info, @QueryParam("abs") String abs) {
        logger.info("abs query: " + abs);
        URI base = null;
        if (abs == null) {
            base = PortProviderUtil.createURI("/", UriInfoSimpleResource.class.getSimpleName());
        } else {
            base = PortProviderUtil.createURI("/" + abs + "/", UriInfoSimpleResource.class.getSimpleName());
        }

        logger.info("BASE URI: " + info.getBaseUri());
        logger.info("Request URI: " + info.getRequestUri());
        logger.info("Absolute URI: " + info.getAbsolutePath());
        Assert.assertEquals(base.getPath(), info.getBaseUri().getPath());
        Assert.assertEquals("/simple", info.getPath());
        return "CONTENT";
    }

    @Path("/simple/fromField")
    @GET
    public String get(@QueryParam("abs") String abs) {
        logger.info("abs query: " + abs);
        URI base = null;
        if (abs == null) {
            base = PortProviderUtil.createURI("/", UriInfoSimpleResource.class.getSimpleName());
        } else {
            base = PortProviderUtil.createURI("/" + abs + "/", UriInfoSimpleResource.class.getSimpleName());
        }

        logger.info("BASE URI: " + myInfo.getBaseUri());
        logger.info("Request URI: " + myInfo.getRequestUri());
        Assert.assertEquals(base.getPath(), myInfo.getBaseUri().getPath());
        Assert.assertEquals("/simple/fromField", myInfo.getPath());
        return "CONTENT";
    }

}
