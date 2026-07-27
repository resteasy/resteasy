/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import java.io.InputStream;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Path("/params/{pathParam}")
@RequestScoped
public class CdiParamResource implements ParamResource {
    private final String cookieParam;
    private final String formParam;
    private final String headerParam;
    private final long matrixParam;
    private final String pathParam;
    private final int queryParam;

    @Inject
    @CookieParam("fieldCookieParam")
    private String fieldCookieParam;
    @Inject
    @FormParam("fieldFormParam")
    private String fieldFormParam;
    @Inject
    @HeaderParam("fieldHeaderParam")
    private String fieldHeaderParam;
    @Inject
    @MatrixParam("fieldMatrixParam")
    private int fieldMatrixParam;
    @Inject
    @PathParam("fieldPathParam")
    private String fieldPathParam;
    @Inject
    @QueryParam("fieldQueryParam")
    private int fieldQueryParam;

    private String methodCookieParam;
    private String methodFormParam;
    private String methodHeaderParam;
    private int methodMatrixParam;
    private String methodPathParam;
    private int methodQueryParam;

    private String methodCookieParamPa;
    private String methodFormParamPa;
    private String methodHeaderParamPa;
    private int methodMatrixParamPa;
    private String methodPathParamPa;
    private int methodQueryParamPa;

    CdiParamResource() {
        cookieParam = null;
        formParam = null;
        headerParam = null;
        matrixParam = -1;
        pathParam = null;
        queryParam = -1;
    }

    @Inject
    public CdiParamResource(@CookieParam("cookieParam") final String cookieParam,
            @FormParam("formParam") final String formParam,
            @HeaderParam("headerParam") final String headerParam,
            @MatrixParam("matrixParam") final long matrixParam,
            @PathParam("pathParam") final String pathParam,
            @QueryParam("queryParam") final int queryParam) {
        this.cookieParam = cookieParam;
        this.formParam = formParam;
        this.headerParam = headerParam;
        this.matrixParam = matrixParam;
        this.pathParam = pathParam;
        this.queryParam = queryParam;
    }

    @Override
    public ParamDescriptor constructor(final InputStream body) {
        return ParamDescriptor.of()
                .setCookieParam(cookieParam)
                .setFormParam(formParam)
                .setHeaderParam(headerParam)
                .setMatrixParam(matrixParam)
                .setPathParam(pathParam)
                .setQueryParam(queryParam);
    }

    @Override
    public ParamDescriptor field(final InputStream body) {
        return ParamDescriptor.of()
                .setCookieParam(fieldCookieParam)
                .setFormParam(fieldFormParam)
                .setHeaderParam(fieldHeaderParam)
                .setMatrixParam(fieldMatrixParam)
                .setPathParam(fieldPathParam)
                .setQueryParam(fieldQueryParam);
    }

    @Override
    public ParamDescriptor method(final InputStream body) {
        return ParamDescriptor.of()
                .setCookieParam(methodCookieParam)
                .setFormParam(methodFormParam)
                .setHeaderParam(methodHeaderParam)
                .setMatrixParam(methodMatrixParam)
                .setPathParam(methodPathParam)
                .setQueryParam(methodQueryParam);
    }

    @Override
    public ParamDescriptor methodParamsAnnotated(final InputStream body) {
        return ParamDescriptor.of()
                .setCookieParam(methodCookieParamPa)
                .setFormParam(methodFormParamPa)
                .setHeaderParam(methodHeaderParamPa)
                .setMatrixParam(methodMatrixParamPa)
                .setPathParam(methodPathParamPa)
                .setQueryParam(methodQueryParamPa);
    }

    @Inject
    @CookieParam("methodCookieParam")
    public void setCookieParam(final String methodCookieParam) {
        this.methodCookieParam = methodCookieParam;
    }

    @Inject
    @FormParam("methodFormParam")
    public void setFormParam(final String methodFormParam) {
        this.methodFormParam = methodFormParam;
    }

    @Inject
    @HeaderParam("methodHeaderParam")
    public void setHeaderParam(final String methodHeaderParam) {
        this.methodHeaderParam = methodHeaderParam;
    }

    @Inject
    @MatrixParam("methodMatrixParam")
    public void setMatrixParam(final int methodMatrixParam) {
        this.methodMatrixParam = methodMatrixParam;
    }

    @Inject
    @PathParam("methodPathParam")
    public void setPathParam(final String methodPathParam) {
        this.methodPathParam = methodPathParam;
    }

    @Inject
    @QueryParam("methodQueryParam")
    public void setQueryParam(final int methodQueryParam) {
        this.methodQueryParam = methodQueryParam;
    }

    @Inject
    public void setCookieParamPa(@CookieParam("methodCookieParamPa") final String cookieParam) {
        this.methodCookieParamPa = cookieParam;
    }

    @Inject
    public void setFormParamPa(@FormParam("methodFormParamPa") final String formParam) {
        this.methodFormParamPa = formParam;
    }

    @Inject
    public void setHeaderParamPa(@HeaderParam("methodHeaderParamPa") final String headerParam) {
        this.methodHeaderParamPa = headerParam;
    }

    @Inject
    public void setMatrixParamPa(@MatrixParam("methodMatrixParamPa") final int matrixParam) {
        this.methodMatrixParamPa = matrixParam;
    }

    @Inject
    public void setPathParamPa(@PathParam("methodPathParamPa") final String pathParam) {
        this.methodPathParamPa = pathParam;
    }

    @Inject
    public void setQueryParamPa(@QueryParam("methodQueryParamPa") final int queryParam) {
        this.methodQueryParamPa = queryParam;
    }
}
