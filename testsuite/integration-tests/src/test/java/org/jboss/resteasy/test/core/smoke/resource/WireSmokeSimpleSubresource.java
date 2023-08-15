package org.jboss.resteasy.test.core.smoke.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

@RequestScoped
public class WireSmokeSimpleSubresource {
    private static Logger logger = Logger.getLogger(WireSmokeSimpleSubresource.class);

    @SuppressWarnings("unused")
    private String foo;

    @Inject
    private UriInfo uriInfo;

    @GET
    @Path("basic")
    @Produces("text/plain")
    public String getBasic() {
        return "basic";
    }

    @Path("subresource")
    public WireSmokeSimpleSubresource getSubresource() {
        logger.info("Subsubresource");
        return new WireSmokeSimpleSubresource();
    }

    @GET
    @Path("testContextParam")
    public void testContextParam() {
        if (uriInfo != null) {
            throw new IllegalStateException("uriInfo is supposed to be null");
        }
    }

}
