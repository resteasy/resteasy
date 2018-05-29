package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.annotations.jaxrs.CookieParam;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/proxy")
public interface RESTEasyParamBasicProxy {
    @POST
    @Path("a/{pathParam3}")
    Response post(
            @CookieParam String cookieParam3,
            @FormParam String formParam3,
            @HeaderParam String headerParam3,
            @MatrixParam String matrixParam3,
            @PathParam String pathParam3,
            @QueryParam String queryParam3);
}
