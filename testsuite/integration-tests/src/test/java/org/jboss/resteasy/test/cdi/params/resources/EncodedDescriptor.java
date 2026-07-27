/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

public class EncodedDescriptor {
    private String queryValue;
    private String pathValue;
    private String decodedQuery;

    public static EncodedDescriptor of() {
        return new EncodedDescriptor();
    }

    public String getQueryValue() {
        return queryValue;
    }

    public EncodedDescriptor setQueryValue(final String queryValue) {
        this.queryValue = queryValue;
        return this;
    }

    public String getPathValue() {
        return pathValue;
    }

    public EncodedDescriptor setPathValue(final String pathValue) {
        this.pathValue = pathValue;
        return this;
    }

    public String getDecodedQuery() {
        return decodedQuery;
    }

    public EncodedDescriptor setDecodedQuery(final String decodedQuery) {
        this.decodedQuery = decodedQuery;
        return this;
    }

    @Override
    public String toString() {
        return "EncodedDescriptor{" + "queryValue='" + queryValue + '\'' +
                ", pathValue='" + pathValue + '\'' +
                ", decodedQuery='" + decodedQuery + '\'' +
                '}';
    }
}
