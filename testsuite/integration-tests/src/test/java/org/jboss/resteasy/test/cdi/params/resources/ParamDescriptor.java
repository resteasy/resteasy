/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class ParamDescriptor {
    private String cookieParam;
    private String formParam;
    private String headerParam;
    private long matrixParam;
    private String pathParam;
    private int queryParam;

    public static ParamDescriptor of() {
        return new ParamDescriptor();
    }

    public String getCookieParam() {
        return cookieParam;
    }

    public ParamDescriptor setCookieParam(final String cookieParam) {
        this.cookieParam = cookieParam;
        return this;
    }

    public String getFormParam() {
        return formParam;
    }

    public ParamDescriptor setFormParam(final String formParam) {
        this.formParam = formParam;
        return this;
    }

    public String getHeaderParam() {
        return headerParam;
    }

    public ParamDescriptor setHeaderParam(final String headerParam) {
        this.headerParam = headerParam;
        return this;
    }

    public long getMatrixParam() {
        return matrixParam;
    }

    public ParamDescriptor setMatrixParam(final long matrixParam) {
        this.matrixParam = matrixParam;
        return this;
    }

    public String getPathParam() {
        return pathParam;
    }

    public ParamDescriptor setPathParam(final String pathParam) {
        this.pathParam = pathParam;
        return this;
    }

    public int getQueryParam() {
        return queryParam;
    }

    public ParamDescriptor setQueryParam(final int queryParam) {
        this.queryParam = queryParam;
        return this;
    }

    @Override
    public String toString() {
        return "ParamDescriptor{" + "cookieParam='" + cookieParam + '\'' +
                ", formParam='" + formParam + '\'' +
                ", headerParam='" + headerParam + '\'' +
                ", matrixParam=" + matrixParam +
                ", pathParam='" + pathParam + '\'' +
                ", queryParam=" + queryParam +
                '}';
    }
}
