/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;

@RequestScoped
public class SearchParams {

    @Inject
    @QueryParam("q")
    private String query;

    @Inject
    @QueryParam("limit")
    @DefaultValue("10")
    private int limit;

    @Inject
    @HeaderParam("Accept-Language")
    private String language;

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "SearchParams{" + "query='" + query + '\'' +
                ", limit=" + limit +
                ", language='" + language + '\'' +
                '}';
    }
}
