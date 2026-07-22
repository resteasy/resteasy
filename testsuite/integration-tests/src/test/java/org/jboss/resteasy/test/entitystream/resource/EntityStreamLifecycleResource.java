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
package org.jboss.resteasy.test.entitystream.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/entity-stream")
public class EntityStreamLifecycleResource {
    @POST
    @Path("/reset")
    public Response reset() {
        EntityStreamLifecycleState.CLOSED.set(false);
        return Response.noContent().build();
    }

    @GET
    @Path("/closed")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean closed() {
        return EntityStreamLifecycleState.CLOSED.get();
    }

    @POST
    @Path("/form")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String form(@FormParam("value") String value) {
        return EntityStreamLifecycleState.CLOSED.get() + ":" + value;
    }

    @POST
    @Path("/entity")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String entity(String value) {
        return EntityStreamLifecycleState.CLOSED.get() + ":" + value;
    }

    @POST
    @Path("/raw")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String raw(InputStream stream) throws IOException {
        boolean closedDuringInvocation = EntityStreamLifecycleState.CLOSED.get();
        String value = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        return closedDuringInvocation + ":" + value;
    }

    @POST
    @Path("/reader")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String reader(Reader reader) throws IOException {
        boolean closedDuringInvocation =
                EntityStreamLifecycleState.CLOSED.get();

        StringBuilder value = new StringBuilder();
        char[] buffer = new char[256];
        int count;

        while ((count = reader.read(buffer)) != -1) {
            value.append(buffer, 0, count);
        }

        return closedDuringInvocation + ":" + value;
    }
}
