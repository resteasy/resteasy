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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
@Priority(Priorities.ENTITY_CODER)
public class EntityStreamLifecyclePreMatchFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!requestContext.hasEntity()
                || !Boolean.parseBoolean(requestContext.getHeaderString(EntityStreamLifecycleFilter.MULTIPLE_REPLACEMENTS))) {
            return;
        }
        InputStream original = requestContext.getEntityStream();
        requestContext.setEntityStream(new FilterInputStream(original) {
            @Override
            public void close() throws IOException {
                EntityStreamLifecycleState.PRE_MATCH_CLOSED.set(true);
                super.close();
            }
        });
    }
}
