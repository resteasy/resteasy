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

package org.jboss.resteasy.test.client.authentication;

import java.security.Principal;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("user")
@PermitAll
public class UserResource {
    @Inject
    SecurityContext securityContext;
    @Inject
    HttpHeaders headers;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response current() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        if (securityContext == null) {
            builder.add("error", "No security context found, but expected.");
            return Response.serverError().entity(builder.build()).build();
        }
        final Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal == null) {
            builder.addNull("username");
        } else {
            builder.add("username", userPrincipal.getName());
        }
        builder.add("authHeader", headers.getHeaderString(HttpHeaders.AUTHORIZATION));
        return Response.ok(builder.build()).build();
    }
}
