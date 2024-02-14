package org.jboss.resteasy.test.resource.basic.resource;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class ConstructedInjectionResource {
    private static Logger logger = Logger.getLogger(ConstructedInjectionResource.class);

    UriInfo myInfo;
    String abs;

    public ConstructedInjectionResource(@Context final UriInfo myInfo, @QueryParam("abs") final String abs) {
        this.myInfo = myInfo;
        this.abs = abs;
    }

    @Path("/simple")
    @GET
    public String get() {
        logger.info("abs query: " + abs);
        URI base = null;
        if (abs == null) {
            base = TestPortProvider.createURI("/");
        } else {
            base = TestPortProvider.createURI("/" + abs + "/");
        }

        logger.info("BASE URI: " + myInfo.getBaseUri());
        logger.info("Request URI: " + myInfo.getRequestUri());
        Assertions.assertEquals(base.getPath() + "ConstructedInjectionTest/", myInfo.getBaseUri().getPath(),
                "The injected base path doesn't match to the expected one");
        Assertions.assertEquals("/simple", myInfo.getPath(),
                "The injected path doesn't match to the expected one");
        return "CONTENT";
    }

}
