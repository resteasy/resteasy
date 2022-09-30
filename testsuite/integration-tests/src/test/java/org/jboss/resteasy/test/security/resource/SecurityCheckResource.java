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

package org.jboss.resteasy.test.security.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.spi.config.Options;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("security")
@ApplicationScoped
@Produces(MediaType.TEXT_PLAIN)
public class SecurityCheckResource {

    @Inject
    private Client client;
    @Inject
    private UriInfo uriInfo;

    @GET
    @Path("system-property/{name}")
    public String systemProperty(@PathParam("name") final String name) {
        return System.getProperty(name);
    }

    @GET
    @Path("env/{name}")
    public String env(@PathParam("name") final String name) {
        return System.getenv(name);
    }

    @GET
    @Path("config/{name}")
    public String config(@PathParam("name") final String name) {
        return ConfigurationFactory.getInstance()
                .getConfiguration()
                .getValue(name, String.class);
    }

    @GET
    @Path("option")
    public String option() {
        return Options.ENABLE_DEFAULT_EXCEPTION_MAPPER.getValue().toString();
    }
}
