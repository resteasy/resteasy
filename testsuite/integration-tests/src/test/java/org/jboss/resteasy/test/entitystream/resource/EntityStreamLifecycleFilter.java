/*
 * JBoss, Home of Professional Open Source.
 * Licensed under the Apache License, Version 2.0 (the "License").
 */
package org.jboss.resteasy.test.entitystream.resource;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.ENTITY_CODER)
public class EntityStreamLifecycleFilter implements ContainerRequestFilter {
    public static final String RESTORE_ORIGINAL = "X-Restore-Original-Entity-Stream";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!requestContext.hasEntity()) {
            return;
        }
        InputStream original = requestContext.getEntityStream();
        InputStream replacement = new FilterInputStream(original) {
            @Override
            public void close() throws IOException {
                EntityStreamLifecycleState.CLOSED.set(true);
                super.close();
            }
        };
        requestContext.setEntityStream(replacement);
        if (Boolean.parseBoolean(requestContext.getHeaderString(RESTORE_ORIGINAL))) {
            requestContext.setEntityStream(original);
        }
    }
}
