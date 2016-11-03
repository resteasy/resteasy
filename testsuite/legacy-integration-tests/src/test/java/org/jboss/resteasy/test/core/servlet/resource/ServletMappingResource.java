package org.jboss.resteasy.test.core.servlet.resource;

import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/")
public class ServletMappingResource {

    @GET
    @Path("basic")
    @Produces("text/plain")
    public String getBasic(@Context UriInfo uriInfo) throws Exception {
        URI uri = uriInfo.getBaseUriBuilder().path(ServletMappingResource.class, "getBasic").build();
        Assert.assertEquals(uri.getPath(), "/resteasy/rest/basic");
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