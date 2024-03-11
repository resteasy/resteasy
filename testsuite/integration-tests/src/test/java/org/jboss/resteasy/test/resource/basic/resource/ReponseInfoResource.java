package org.jboss.resteasy.test.resource.basic.resource;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.resource.basic.ReponseInfoTest;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class ReponseInfoResource {
    private static Logger logger = Logger.getLogger(ReponseInfoResource.class);

    @Path("/simple")
    @GET
    public String get(@QueryParam("abs") String abs) {
        logger.info("abs query: " + abs);
        URI base;
        if (abs == null) {
            base = PortProviderUtil.createURI("/new/one", ReponseInfoTest.class.getSimpleName());
        } else {
            base = PortProviderUtil.createURI("/" + abs + "/new/one", ReponseInfoTest.class.getSimpleName());
        }
        Response response = Response.temporaryRedirect(URI.create("new/one")).build();
        URI uri = (URI) response.getMetadata().getFirst(HttpHeaderNames.LOCATION);
        logger.info("Location uri: " + uri);
        Assertions.assertEquals(base.getPath(), uri.getPath(), "Wrong path from URI");
        return "CONTENT";
    }
}
