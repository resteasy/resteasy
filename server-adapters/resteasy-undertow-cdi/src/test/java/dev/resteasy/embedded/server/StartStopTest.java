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

package dev.resteasy.embedded.server;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServers;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class StartStopTest {

    @Test
    public void startStopStart() {
        final SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().build();
        final EmbeddedServer server = EmbeddedServers.findServer();
        final ResteasyDeployment deployment = server.getDeployment();
        deployment.setApplication(new RootApplication());
        deployment.getResourceClasses().add(GreeterResource.class.getName());
        server.start(configuration);
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(configuration.baseUriBuilder().path("/greet/Nina"))
                    .request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("Hello Nina", response.readEntity(String.class));

            // Stop the server, then restart it
            server.stop();
            server.start(configuration);
            response = client.target(configuration.baseUriBuilder().path("/greet/Nina"))
                    .request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("Hello Nina", response.readEntity(String.class));
        } finally {
            server.stop();
        }
    }

    @ApplicationScoped
    public static class Greeter {

        public String greet(final String name) {
            return "Hello " + name;
        }
    }

    @ApplicationPath("/")
    public static class RootApplication extends Application {
    }

    @Path("/greet")
    @ApplicationScoped
    public static class GreeterResource {
        @Inject
        private Greeter greeter;

        @GET
        @Path("/{name}")
        public Response greet(@PathParam("name") final String name) {
            return Response.ok(greeter.greet(name)).build();
        }
    }
}
