/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import java.lang.annotation.Annotation;
import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("/check-qualifier")
public class CdiQualifierResource {
    private static final Set<Class<? extends Annotation>> SPEC_ANNOTATIONS = Set.of(
            BeanParam.class,
            CookieParam.class,
            FormParam.class,
            HeaderParam.class,
            MatrixParam.class,
            PathParam.class,
            QueryParam.class);

    @Inject
    private BeanManager beanManager;

    @GET
    public JsonArray checkAnnotations() {
        final JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Class<? extends Annotation> annotation : SPEC_ANNOTATIONS) {
            builder.add(Json.createObjectBuilder().add(annotation.getName(), beanManager.isQualifier(annotation)));
        }
        return builder.build();
    }
}
