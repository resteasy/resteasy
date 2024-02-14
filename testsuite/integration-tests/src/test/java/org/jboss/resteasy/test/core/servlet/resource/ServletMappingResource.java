package org.jboss.resteasy.test.core.servlet.resource;

import java.net.URI;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class ServletMappingResource {

    @GET
    @Path("basic")
    @Produces("text/plain")
    public String getBasic(@Context UriInfo uriInfo) throws Exception {
        URI uri = uriInfo.getBaseUriBuilder().path(ServletMappingResource.class, "getBasic").build();
        Assertions.assertEquals(uri.getPath(), "/resteasy/rest/basic");
        return "basic";
    }

    @PUT
    @Path("basic")
    @Consumes("text/plain")
    public void putBasic(String body) {
    }

    @GET
    @Path("queryParam")
    @Produces("text/plain")
    public String getQueryParam(@QueryParam("param") String param) {
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

    @POST
    @Path("formtestit")
    @Produces("text/plain")
    public String postForm(@FormParam("value") String value, @Context HttpHeaders headers) {
        if (value == null) {
            throw new RuntimeException("VALUE WAS NULL");
        }
        return value;
    }
}
