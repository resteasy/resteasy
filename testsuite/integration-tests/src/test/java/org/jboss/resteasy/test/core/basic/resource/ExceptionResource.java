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

package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/exception")
public class ExceptionResource {
    public static final Response WAE_RESPONSE = Response.status(Response.Status.FORBIDDEN)
            .entity("Not allowed from test")
            .build();
    public static final String EXCEPTION_MESSAGE = "This is a test exception";

    @GET
    public Response throwException() {
        throw new RuntimeException(EXCEPTION_MESSAGE);
    }

    @GET
    @Path("/wae")
    public Response throwWae() {
        throw new WebApplicationException(WAE_RESPONSE);
    }

    @GET
    @Path("/not-impl")
    public Response notImpl() {
        throw new UnsupportedOperationException("Messages should not be seen.");
    }
}
