package org.jboss.resteasy.test.providers.custom.resource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("")
public class CustomConstrainedFeatureResource {

    public static final String ERROR_SERVER_FEATURE = "CustomServerConstrainedFeature must be invoked on the server runtime";
    public static final String ERROR_CLIENT_FEATURE = "CustomClientConstrainedFeature must be invoked on the client runtime";
    private Logger logger = Logger.getLogger(CustomConstrainedFeatureResource.class);

    @GET
    @Path("test-custom-feature")
    @Produces("text/plain")
    public Response test() {
        try {
            // only server runtime feature must be invoked
            assertTrue(CustomServerConstrainedFeature.wasInvoked(), ERROR_SERVER_FEATURE);
            assertFalse(CustomClientConstrainedFeature.wasInvoked(), ERROR_CLIENT_FEATURE);
        } catch (AssertionError e) {
            logger.error(e);
            return Response.status(500).entity(e.getLocalizedMessage()).build();
        }
        return Response.status(200).build();
    }
}
