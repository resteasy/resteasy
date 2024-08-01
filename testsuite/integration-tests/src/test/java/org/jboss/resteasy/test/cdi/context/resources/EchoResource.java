/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.cdi.context.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/echo")
@RequestScoped
public class EchoResource {

    public static final String RESPONSE_TYPE_HEADER = "response-type";
    @Inject
    private HttpHeaders headers;

    @Inject
    private Providers providers;

    public enum ResponseType {
        TEST
    }

    @POST
    public Response echo(final JsonObject json) {

        final var builder = Json.createObjectBuilder();
        final var headerArray = Json.createArrayBuilder();
        headers.getRequestHeaders().forEach((key, value) -> {
            final var headerBuilder = Json.createObjectBuilder();
            headerBuilder.add(key, Json.createArrayBuilder(value));
            headerArray.add(headerBuilder);
        });
        builder.add("headers", headerArray);
        final var resolver = providers.getContextResolver(JsonToString.class, MediaType.APPLICATION_JSON_TYPE);
        if (resolver != null) {
            builder.add("entity", resolver.getContext(JsonToString.class)
                    .objectToString(json));
        } else {
            throw new BadRequestException("Failed to find the JsonToString provider for the request");
        }

        return Response.ok(builder.build())
                .header(RESPONSE_TYPE_HEADER, ResponseType.TEST)
                .build();
    }
}
