package org.jboss.resteasy.test.core.smoke.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/")
public interface ResourceWithInterfaceSimpleClient {
    @GET
    @Path("basic")
    @Produces("text/plain")
    String getBasic();

    @PUT
    @Path("basic")
    @Consumes("text/plain")
    void putBasic(String body);

    @GET
    @Path("queryParam")
    @Produces("text/plain")
    String getQueryParam(@QueryParam("param") String param);

    @GET
    @Path("matrixParam")
    @Produces("text/plain")
    String getMatrixParam(@MatrixParam("param") String param);

    @GET
    @Path("uriParam/{param}")
    @Produces("text/plain")
    int getUriParam(@PathParam("param") int param);

    default String getBasicThroughDefaultMethod() {
        return getBasic();
    }

    default String defaultQueryParameter(final String param) {
        return getQueryParam(param);
    }
}
