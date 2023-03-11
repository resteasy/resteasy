package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.CookieParam;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/basic")
public class RESTEasyParamBasicResource {

    protected static final Logger logger = Logger.getLogger(RESTEasyParamBasicResource.class.getName());

    private String cookieParam0;
    @CookieParam
    private String cookieParam1;
    private String cookieParam2;

    private String formParam0;
    @FormParam
    private String formParam1;
    private String formParam2;

    private String headerParam0;
    @HeaderParam
    private String headerParam1;
    private String headerParam2;

    private String matrixParam0;
    @MatrixParam
    private String matrixParam1;
    private String matrixParam2;

    private String pathParam0;
    @PathParam
    private String pathParam1;
    private String pathParam2;

    private String queryParam0;
    @QueryParam
    private String queryParam1;
    private String queryParam2;

    public RESTEasyParamBasicResource(
            final @CookieParam String cookieParam0,
            final @FormParam String formParam0,
            final @HeaderParam String headerParam0,
            final @MatrixParam String matrixParam0,
            final @PathParam String pathParam0,
            final @QueryParam String queryParam0) {
        this.cookieParam0 = cookieParam0;
        this.formParam0 = formParam0;
        this.headerParam0 = headerParam0;
        this.matrixParam0 = matrixParam0;
        this.pathParam0 = pathParam0;
        this.queryParam0 = queryParam0;
    }

    public String getCookieParam2() {
        return cookieParam2;
    }

    @CookieParam
    public void setCookieParam2(String cookieParam2) {
        this.cookieParam2 = cookieParam2;
    }

    public String getFormParam2() {
        return formParam2;
    }

    @FormParam
    public void setFormParam2(String formParam2) {
        this.formParam2 = formParam2;
    }

    public String getPathParam2() {
        return pathParam2;
    }

    public String getHeaderParam2() {
        return headerParam2;
    }

    @HeaderParam
    public void setHeaderParam2(String headerParam2) {
        this.headerParam2 = headerParam2;
    }

    public String getMatrixParam2() {
        return matrixParam2;
    }

    @MatrixParam
    public void setMatrixParam2(String matrixParam2) {
        this.matrixParam2 = matrixParam2;
    }

    @PathParam
    public void setPathParam2(String pathParam2) {
        this.pathParam2 = pathParam2;
    }

    public String getQueryParam2() {
        return queryParam2;
    }

    @QueryParam
    public void setQueryParam2(String queryParam2) {
        this.queryParam2 = queryParam2;
    }

    @POST
    @Path("a/{pathParam0}/{pathParam1}/{pathParam2}/{pathParam3}")
    public Response post(
            @CookieParam String cookieParam3,
            @FormParam String formParam3,
            @HeaderParam String headerParam3,
            @MatrixParam String matrixParam3,
            @PathParam String pathParam3,
            @QueryParam String queryParam3) {

        StringBuilder details = new StringBuilder();
        details.append("cookieParam0: " + cookieParam0 + "\n");
        details.append("cookieParam1: " + cookieParam1 + "\n");
        details.append("cookieParam2: " + cookieParam2 + "\n");
        details.append("cookieParam3: " + cookieParam3 + "\n");

        details.append("formParam0: " + formParam0 + "\n");
        details.append("formParam1: " + formParam1 + "\n");
        details.append("formParam2: " + formParam2 + "\n");
        details.append("formParam3: " + formParam3 + "\n");

        details.append("headerParam0: " + headerParam0 + "\n");
        details.append("headerParam1: " + headerParam1 + "\n");
        details.append("headerParam2: " + headerParam2 + "\n");
        details.append("headerParam3: " + headerParam3 + "\n");

        details.append("matrixParam0: " + matrixParam0 + "\n");
        details.append("matrixParam1: " + matrixParam1 + "\n");
        details.append("matrixParam2: " + matrixParam2 + "\n");
        details.append("matrixParam3: " + matrixParam3 + "\n");

        details.append("pathParam0: " + pathParam0 + "\n");
        details.append("pathParam1: " + pathParam1 + "\n");
        details.append("pathParam2: " + pathParam2 + "\n");
        details.append("pathParam3: " + pathParam3 + "\n");

        details.append("queryParam0: " + queryParam0 + "\n");
        details.append("queryParam1: " + queryParam1 + "\n");
        details.append("queryParam2: " + queryParam2 + "\n");
        details.append("queryParam3: " + queryParam3 + "\n");

        logger.info(details);

        if (!"cookieParam0".equals(cookieParam0)
                || !"cookieParam1".equals(cookieParam1)
                || !"cookieParam2".equals(cookieParam2)
                || !"cookieParam3".equals(cookieParam3)) {
            logger.error("cookie error");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!"formParam0".equals(formParam0)
                || !"formParam1".equals(formParam1)
                || !"formParam2".equals(formParam2)
                || !"formParam3".equals(formParam3)) {
            logger.error("form error");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!"headerParam0".equals(headerParam0)
                || !"headerParam1".equals(headerParam1)
                || !"headerParam2".equals(headerParam2)
                || !"headerParam3".equals(headerParam3)) {
            logger.error("header error");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!"matrixParam0".equals(matrixParam0)
                || !"matrixParam1".equals(matrixParam1)
                || !"matrixParam2".equals(matrixParam2)
                || !"matrixParam3".equals(matrixParam3)) {
            logger.error("matrix error");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!"pathParam0".equals(pathParam0)
                || !"pathParam1".equals(pathParam1)
                || !"pathParam2".equals(pathParam2)
                || !"pathParam3".equals(pathParam3)) {
            logger.error("path error");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!"queryParam0".equals(queryParam0)
                || !"queryParam1".equals(queryParam1)
                || !"queryParam2".equals(queryParam2)
                || !"queryParam3".equals(queryParam3)) {
            logger.error("query error");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }
}
