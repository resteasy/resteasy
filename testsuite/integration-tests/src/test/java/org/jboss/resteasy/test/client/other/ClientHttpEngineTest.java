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

package org.jboss.resteasy.test.client.other;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * A test to ensure including a {@link org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory} implementation
 * works when deployed to a server.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RequiresModule(value = "org.jboss.resteasy.resteasy-client-api", minVersion = "7.0.0.Alpha4")
abstract class ClientHttpEngineTest {

    private final String agentName;

    @Inject
    private Client client;

    @ArquillianResource
    private URI uri;

    protected ClientHttpEngineTest(final String agentName) {
        this.agentName = agentName;
    }

    protected static WebArchive createDeployment(final Class<?> test, final File[] libs) {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, test.getSimpleName() + ".war")
                .addClasses(UserAgentResource.class, TestApplication.class, ClientHttpEngineTest.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        if (libs != null) {
            war.addAsLibraries(libs);
        }
        return war;
    }

    @Test
    public void injectedClient() {
        try (Response response = client.target(uri).request().get()) {
            Assertions.assertEquals(200, response.getStatus(),
                    () -> String.format("Failed to find User-Agent: %s", response.readEntity(String.class)));
            final String userAgent = response.readEntity(String.class);
            Assertions.assertTrue(userAgent.contains(agentName),
                    () -> String.format("Expected a User-Agent of Jetty in %s", userAgent));
        }
    }

    @Test
    public void builtClient() {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(uri).request().get()) {
            Assertions.assertEquals(200, response.getStatus(),
                    () -> String.format("Failed to find User-Agent: %s", response.readEntity(String.class)));
            final String userAgent = response.readEntity(String.class);
            Assertions.assertTrue(userAgent.contains(agentName),
                    () -> String.format("Expected a User-Agent of Jetty in %s", userAgent));
        }
    }

    @Path("/")
    @RequestScoped
    @Produces(MediaType.APPLICATION_JSON)
    public static class UserAgentResource {
        @Inject
        private HttpHeaders headers;

        @GET
        public String userAgent(@HeaderParam(HttpHeaders.USER_AGENT) final String agent) {
            if (agent != null) {
                return agent;
            }
            final StringBuilder builder = new StringBuilder();
            builder.append("Could not find User-Agent header in:").append(System.lineSeparator());
            appendHeaders(builder);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(builder.toString())
                    .build());
        }

        private void appendHeaders(final StringBuilder sb) {
            try (
                    StringWriter writer = new StringWriter();
                    JsonGenerator generator = Json.createGeneratorFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true))
                            .createGenerator((writer))) {
                generator.writeStartObject();
                generator.writeStartObject("headers");
                headers.getRequestHeaders().forEach((name, values) -> {
                    generator.writeStartArray(name);
                    values.forEach(generator::write);
                    generator.writeEnd();
                });
                generator.writeEnd();
                generator.writeEnd();
                generator.flush();
                sb.append(writer);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
