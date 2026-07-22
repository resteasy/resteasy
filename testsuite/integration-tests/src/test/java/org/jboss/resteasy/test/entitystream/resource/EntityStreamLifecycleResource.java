/*
 * JBoss, Home of Professional Open Source.
 * Licensed under the Apache License, Version 2.0 (the "License").
 */
package org.jboss.resteasy.test.entitystream.resource;

import java.io.IOException;
import java.io.InputStream;
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
}
