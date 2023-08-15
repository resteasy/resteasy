/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.client.cdi.resources;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RequestScoped
public class ContextAndInjectionFilter implements ContainerRequestFilter {

    @Inject
    private UriInfo uriInfo;

    @Inject
    private TestBean testBean;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        if (uriInfo == null) {
            builder.addNull("uriInfo");
        } else {
            builder.add("uriInfo", uriInfo.getPath());
        }
        if (testBean == null) {
            builder.addNull("testBean");
        } else {
            builder.add("testBean", testBean.getPath());
        }
        requestContext.abortWith(Response.ok(builder.build()).build());
    }
}
