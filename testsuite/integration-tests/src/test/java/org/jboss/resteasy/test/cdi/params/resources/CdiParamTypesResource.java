/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.PathSegment;

@Path("/types/{paths:.+}")
@RequestScoped
public class CdiParamTypesResource {

    @Inject
    @QueryParam("list")
    private List<String> listParam;

    @Inject
    @QueryParam("set")
    private Set<String> setParam;

    @Inject
    @QueryParam("sortedSet")
    private SortedSet<String> sortedSetParam;

    @Inject
    @QueryParam("array")
    private String[] arrayParam;

    @Inject
    @QueryParam("wrapper")
    private Integer wrapperParam;

    @Inject
    @QueryParam("bool")
    private Boolean booleanParam;

    @Inject
    @QueryParam("enum")
    private ParamEnum enumParam;

    @Inject
    @QueryParam("default")
    @DefaultValue("fallback")
    private String defaultParam;

    @Inject
    @QueryParam("defaultInt")
    @DefaultValue("42")
    private int defaultIntParam;

    @Inject
    @PathParam("paths")
    private List<PathSegment> listPaths;

    @Inject
    @PathParam("paths")
    private PathSegment singlePath;

    CdiParamTypesResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TypesDescriptor get() {
        return TypesDescriptor.of()
                .setListParam(listParam)
                .setSetParam(setParam)
                .setSortedSetParam(sortedSetParam)
                .setArrayParam(arrayParam)
                .setWrapperParam(wrapperParam)
                .setBooleanParam(booleanParam)
                .setEnumParam(enumParam)
                .setDefaultParam(defaultParam)
                .setDefaultIntParam(defaultIntParam)
                .setListPaths(listPaths.stream().map(PathSegment::getPath).toList())
                .setSinglePath(singlePath.getPath());
    }
}
