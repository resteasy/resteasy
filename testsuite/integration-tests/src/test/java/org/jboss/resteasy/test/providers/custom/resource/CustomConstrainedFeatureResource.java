package org.jboss.resteasy.test.providers.custom.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
          assertTrue(ERROR_SERVER_FEATURE, CustomServerConstrainedFeature.wasInvoked());
          assertFalse(ERROR_CLIENT_FEATURE, CustomClientConstrainedFeature.wasInvoked());
       } catch (AssertionError e) {
          logger.error(e);
          return Response.status(500).entity(e.getLocalizedMessage()).build();
       }
       return Response.status(200).build();
    }
}
