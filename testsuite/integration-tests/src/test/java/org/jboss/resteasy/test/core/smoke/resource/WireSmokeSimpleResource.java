package org.jboss.resteasy.test.core.smoke.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/")
public class WireSmokeSimpleResource {
    private static Logger logger = Logger.getLogger(WireSmokeSimpleResource.class);

    @GET
    @Path("basic")
    @Produces("text/plain")
    public String getBasic() {
        logger.info("getBasic()");
        return "basic";
    }

    @PUT
    @Path("basic")
    @Consumes("text/plain")
    public void putBasic(String body) {
        logger.info(body);
    }

    @GET
    @Path("queryParam")
    @Produces("text/plain")
    public String getQueryParam(@QueryParam("param") String param) {
        logger.info("query param: " + param);
        return param;
    }

    @GET
    @Path("matrixParam")
    @Produces("text/plain")
    public String getMatrixParam(@MatrixParam("param") String param) {
        return param;
    }

    @GET
    @Path("uriParam/{param}")
    @Produces("text/plain")
    public int getUriParam(@PathParam("param") int param) {
        return param;
    }

    @GET
    @Path("header")
    public Response getHeader() {
        return Response.ok().header("header", "headervalue").build();
    }
}
