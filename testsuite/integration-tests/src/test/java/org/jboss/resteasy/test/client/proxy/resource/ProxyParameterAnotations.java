package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.annotations.jaxrs.CookieParam;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 1/16/19.
 */
@Path("/")
public interface ProxyParameterAnotations {
    @Path("QueryParam")
    @GET
    String executeQueryParam(@QueryParam String queryParam);

    @Path("HeaderParam")
    @GET
    String executeHeaderParam(@HeaderParam String headerParam);

    @Path("CookieParam")
    @GET
    String executeCookieParam(@CookieParam String cookieParam);

    @Path("PathParam/{pathParam}")
    @GET
    String executePathParam(@PathParam String pathParam);

    @Path("FormParam")
    @POST
    String executeFormParam(@FormParam String formParam);

    @Path("MatrixParam")
    @GET
    String executeMatrixParam(@MatrixParam String matrixParam);

    @Path("AllParams/{pathParam}")
    @POST
    String executeAllParams(@QueryParam String queryParam,
                            @HeaderParam String headerParam,
                            @CookieParam String cookieParam,
                            @PathParam String pathParam,
                            @FormParam String formParam,
                            @MatrixParam String matrixParam);
}