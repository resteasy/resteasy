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

package org.jboss.resteasy.test.providers.jackson2.objectmapper.resources;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.wildfly.common.annotation.NotNull;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class CrudResource<T extends IdEntry> {

    private final AtomicReference<String> contextPath = new AtomicReference<>();

    @Inject
    private UriInfo uriInfo;

    @GET
    public Set<T> get() {
        return repository().get();
    }

    @GET
    @Path("{id}")
    public T get(@PathParam("id") long id) {
        return repository().findById(id);
    }

    @POST
    @Path("add")
    public Response add(@NotNull final T entry) {
        repository().add(entry);
        return Response.created(uriInfo.getBaseUriBuilder().path(resolveContextPath() + entry.getId()).build()).build();
    }

    @PUT
    @Path("update")
    public Response update(@NotNull final T entry) {
        repository().update(entry);
        return Response.noContent().build();
    }

    abstract GenericRepository<T> repository();

    private String resolveContextPath() {
        return contextPath.updateAndGet(s -> {
            if (s != null) {
                return s;
            }
            String contextPath = "/";
            final Path path = CrudResource.this.getClass().getAnnotation(Path.class);
            if (path != null) {
                contextPath = path.value();
                if (!contextPath.endsWith("/")) {
                    contextPath = contextPath + "/";
                }
            }
            return contextPath;
        });
    }
}
