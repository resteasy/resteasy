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
@Path("/empty-names/{pathParam}")
@RequestScoped
public class CdiParamEmptyNamesResource implements ParamResource {
    private final String cookieParam;
    private final String formParam;
    private final String headerParam;
    private final long matrixParam;
    private final String pathParam;
    private final int queryParam;

    @Inject
    @CookieParam("")
    private String fieldCookieParam;
    @Inject
    @FormParam("")
    private String fieldFormParam;
    @Inject
    @HeaderParam("")
    private String fieldHeaderParam;
    @Inject
    @MatrixParam("")
    private int fieldMatrixParam;
    @Inject
    @PathParam("")
    private String fieldPathParam;
    @Inject
    @QueryParam("")
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

    CdiParamEmptyNamesResource() {
        cookieParam = null;
        formParam = null;
        headerParam = null;
        matrixParam = -1;
        pathParam = null;
        queryParam = -1;
    }

    @Inject
    public CdiParamEmptyNamesResource(@CookieParam("") final String cookieParam,
            @FormParam("") final String formParam,
            @HeaderParam("") final String headerParam,
            @MatrixParam("") final long matrixParam,
            @PathParam("") final String pathParam,
            @QueryParam("") final int queryParam) {
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
    @CookieParam("")
    public void setMethodCookieParam(final String methodCookieParam) {
        this.methodCookieParam = methodCookieParam;
    }

    @Inject
    @FormParam("")
    public void setMethodFormParam(final String methodFormParam) {
        this.methodFormParam = methodFormParam;
    }

    @Inject
    @HeaderParam("")
    public void setMethodHeaderParam(final String methodHeaderParam) {
        this.methodHeaderParam = methodHeaderParam;
    }

    @Inject
    @MatrixParam("")
    public void setMethodMatrixParam(final int methodMatrixParam) {
        this.methodMatrixParam = methodMatrixParam;
    }

    @Inject
    @PathParam("")
    public void setMethodPathParam(final String methodPathParam) {
        this.methodPathParam = methodPathParam;
    }

    @Inject
    @QueryParam("")
    public void setMethodQueryParam(final int methodQueryParam) {
        this.methodQueryParam = methodQueryParam;
    }

    @Inject
    public void setCookieParam(@CookieParam("") final String methodCookieParamPa) {
        this.methodCookieParamPa = methodCookieParamPa;
    }

    @Inject
    public void setFormParam(@FormParam("") final String methodFormParamPa) {
        this.methodFormParamPa = methodFormParamPa;
    }

    @Inject
    public void setHeaderParam(@HeaderParam("") final String methodHeaderParamPa) {
        this.methodHeaderParamPa = methodHeaderParamPa;
    }

    @Inject
    public void setMatrixParam(@MatrixParam("") final int methodMatrixParamPa) {
        this.methodMatrixParamPa = methodMatrixParamPa;
    }

    @Inject
    public void setPathParam(@PathParam("") final String methodPathParamPa) {
        this.methodPathParamPa = methodPathParamPa;
    }

    @Inject
    public void setQueryParam(@QueryParam("") final int methodQueryParamPa) {
        this.methodQueryParamPa = methodQueryParamPa;
    }
}
