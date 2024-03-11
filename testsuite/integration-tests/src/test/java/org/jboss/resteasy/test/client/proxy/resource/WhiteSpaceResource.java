package org.jboss.resteasy.test.client.proxy.resource;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.junit.jupiter.api.Assertions;

@Path(value = "/sayhello")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class WhiteSpaceResource {

    String SPACES_REQUEST = "something something";
    private static final Logger logger = Logger.getLogger(WhiteSpaceResource.class);

    @Inject
    UriInfo info;

    @GET
    @Path("/en/{in}")
    @Produces("text/plain")
    public String echo(@PathParam(value = "in") String in) {
        Assertions.assertEquals(SPACES_REQUEST, in);
        List<String> params = info.getPathParameters(true).get("in");
        logger.info("DECODE" + params.get(0));

        params = info.getPathParameters(false).get("in");
        logger.info("ENCODE" + params.get(0));

        return in;
    }
}
