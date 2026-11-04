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

package org.jboss.resteasy.test.cdi.injection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.RequiredInjectableContextResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RequiredInjectableContextTest {

    private static Client client;

    @ArquillianResource
    private URL url;

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, RequiredInjectableContextTest.class.getSimpleName() + ".war")
                .addClasses(RequiredInjectableContextResource.class, RootApplication.class, TestExceptionMapper.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @BeforeAll
    public static void initClient() {
        client = ClientBuilder.newBuilder()
                .build();
    }

    @AfterAll
    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void application() throws Exception {
        final Response response = get("application/test.property");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("test value", response.readEntity(String.class));
    }

    @Test
    public void client() throws Exception {
        final Response response = get("client/request");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void configuration() throws Exception {
        final Response response = get("configuration");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals(RuntimeType.SERVER.name(), response.readEntity(String.class));
    }

    @Test
    public void httpHeader() throws Exception {
        final Response response = get("httpHeaders/test-header");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("test-value", response.readEntity(String.class));
    }

    @Test
    public void httpRequest() throws Exception {
        final Response response = get("httpRequest");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void provider() throws Exception {
        final Response response = get("providers");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        final String value = response.readEntity(String.class);
        Assertions.assertTrue(value.contains(TestExceptionMapper.class.getSimpleName()),
                String.format("Value expected to contain %s but was %s", TestExceptionMapper.class.getSimpleName(), value));
    }

    @Test
    public void request() throws Exception {
        final Response response = get("request");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void resourceContext() throws Exception {
        final Response response = get("resourceContext");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertTrue(response.readEntity(String.class)
                .startsWith(RequiredInjectableContextResource.class.getCanonicalName()));
    }

    @Test
    public void resourceInfo() throws Exception {
        final Response response = get("resourceInfo");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals(response.readEntity(String.class), "resourceInfo");
    }

    @Test
    public void securityContext() throws Exception {
        final Response response = get("securityContext");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("false", response.readEntity(String.class));
    }

    @Test
    public void uriInfo() throws Exception {
        final Response response = get("uriInfo");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("/inject/uriInfo", response.readEntity(String.class));
    }

    @Test
    public void sse() throws Exception {
        final WebTarget target = client.target(TestUtil.generateUri(url, "inject/sse"));
        final CompletableFuture<String> cf = new CompletableFuture<>();
        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(event -> {
                try {
                    cf.complete(event.readData());
                } catch (Throwable t) {
                    cf.completeExceptionally(t);
                }
            });
            source.open();
            Thread.sleep(500L);
        }
        Assertions.assertEquals("test", cf.get(5, TimeUnit.SECONDS));
    }

    private Response get(final String path) throws URISyntaxException {
        return client.target(TestUtil.generateUri(url, "inject/" + path))
                .request()
                .header("test-header", "test-value")
                .get();
    }

    @Provider
    public static class TestExceptionMapper implements ExceptionMapper<IllegalStateException> {

        @Override
        public Response toResponse(final IllegalStateException exception) {
            final StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            return Response.serverError()
                    .entity(writer.toString())
                    .build();
        }
    }
}
