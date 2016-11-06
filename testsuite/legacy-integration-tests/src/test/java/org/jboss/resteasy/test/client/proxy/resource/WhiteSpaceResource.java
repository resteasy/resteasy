package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path(value = "/sayhello")
public class WhiteSpaceResource {

    String SPACES_REQUEST = "something something";
    private static final Logger logger = Logger.getLogger(WhiteSpaceResource.class);

    @Context
    UriInfo info;

    @GET
    @Path("/en/{in}")
    @Produces("text/plain")
    public String echo(@PathParam(value = "in") String in) {
        Assert.assertEquals(SPACES_REQUEST, in);
        List<String> params = info.getPathParameters(true).get("in");
        logger.info("DECODE" + params.get(0));

        params = info.getPathParameters(false).get("in");
        logger.info("ENCODE" + params.get(0));

        return in;
    }
}
