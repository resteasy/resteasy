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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.jandex.Index;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(value = InjectionTest.RootApplication.class, configFactory = InjectionTest.InjectionConfiguration.class)
public class InjectionTest {
    public static class InjectionConfiguration implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration() {
            try {
                final Index index = Index.of(Greeter.class, RootApplication.class, GreeterResource.class);
                return TestEnvironment.createConfig(index);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    public void application(@RequestPath("inject/greet/app") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("Hello App", response.readEntity(String.class));
        }
    }

    @Test
    public void resource(@RequestPath("inject/greet/Violet") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("Hello Violet", response.readEntity(String.class));
        }
    }

    @ApplicationScoped
    public static class Greeter {

        public String greet(final String name) {
            return "Hello " + name;
        }
    }

    @ApplicationPath("/inject")
    @ApplicationScoped
    public static class RootApplication extends Application {
        @Inject
        private Greeter greeter;

        @Override
        public Map<String, Object> getProperties() {
            return Map.of("app.greter", greeter);
        }

        public String greet(final String name) {
            return greeter.greet(name);
        }
    }

    @Path("/greet")
    @RequestScoped
    public static class GreeterResource {
        @Inject
        private Greeter greeter;

        @Inject
        private RootApplication application;

        @GET
        @Path("/{name}")
        public Response greet(@PathParam("name") final String name) {
            return Response.ok(greeter.greet(name)).build();
        }

        @GET
        @Path("/app/")
        public Response greetFromApp() {
            return Response.ok(application.greet("App")).build();
        }
    }
}
