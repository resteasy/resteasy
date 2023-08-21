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

package org.jboss.resteasy.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.Sse;

import org.jboss.resteasy.plugins.providers.sse.SseImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * CDI producers for Jakarta RESTful Web Services types and RESTEasy specific types.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Singleton
public class ContextProducers {

    @Dependent
    @Produces
    public Configuration configuration() {
        return getProviderFactory().getContextData(Configuration.class);
    }

    @RequestScoped
    @Produces
    public HttpHeaders httpHeaders() {
        return getProviderFactory().getContextData(HttpHeaders.class);
    }

    @RequestScoped
    @Produces
    public HttpRequest httpRequest() {
        return getProviderFactory().getContextData(HttpRequest.class);
    }

    @ApplicationScoped
    @Produces
    public Providers providers() {
        return getProviderFactory();
    }

    @RequestScoped
    @Produces
    public Request request() {
        return getProviderFactory().getContextData(Request.class);
    }

    @ApplicationScoped
    @Produces
    public ResourceContext resourceContext() {
        return getProviderFactory().getContextData(ResourceContext.class);
    }

    @RequestScoped
    @Produces
    public ResourceInfo resourceInfo() {
        return getProviderFactory().getContextData(ResourceInfo.class);
    }

    @RequestScoped
    @Produces
    public SecurityContext securityContext() {
        return getProviderFactory().getContextData(SecurityContext.class);
    }

    @RequestScoped
    @Produces
    public UriInfo uriInfo() {
        return getProviderFactory().getContextData(UriInfo.class);
    }

    @ApplicationScoped
    @Produces
    public Sse sse() {
        return new SseImpl();
    }

    private ResteasyProviderFactory getProviderFactory() {
        return ResteasyProviderFactory.getInstance();
    }
}
