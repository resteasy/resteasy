package org.jboss.resteasy.test.resteasy1056;

import jakarta.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

/**
 *
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright June 7, 2014
 */
@Path("/")
public class TestResource {

    private static final Logger LOG = Logger.getLogger(TestResource.class);

    @GET
    @Path("test/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test(@Min(7) @PathParam("param") int param) {
        LOG.info("param: " + param);
        return Response.ok().entity(param).build();
    }
}
