/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
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

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
abstract class AbstractRequiredInjectableContextTest {

    private static Client client;

    private final String resourcePath;

    @ArquillianResource
    private URL url;

    AbstractRequiredInjectableContextTest(final String resourcePath) {
        this.resourcePath = resourcePath;
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

    static WebArchive defaultDeployment(final Class<? extends AbstractRequiredInjectableContextTest> testClass) {
        return ShrinkWrap.create(WebArchive.class, testClass.getSimpleName() + ".war")
                .addClasses(AbstractRequiredInjectableContextTest.class, RootApplication.class, TestExceptionMapper.class);
    }

    @Test
    public void application() throws Exception {
        final Response response = get("application/test.property");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("test value", response.readEntity(String.class));
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
        Assertions.assertEquals("ok", response.readEntity(String.class));
    }

    @Test
    public void resourceInfo() throws Exception {
        final Response response = get("resourceInfo");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("resourceInfo", response.readEntity(String.class));
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
        Assertions.assertEquals(String.format("%s/uriInfo", resourcePath), response.readEntity(String.class));
    }

    @Test
    public void sse() throws Exception {
        final WebTarget target = client.target(TestUtil.generateUri(url, resourcePath + "/sse"));
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

    @Test
    public void servletRequest() throws Exception {
        try (Response response = get("servletRequest")) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("GET", response.readEntity(String.class));
        }
    }

    @Test
    public void servletResponse() throws Exception {
        try (Response response = get("servletResponse")) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("200", response.readEntity(String.class));
        }
    }

    @Test
    public void servletContext() throws Exception {
        try (Response response = get("servletContext")) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("/" + getClass().getSimpleName(),
                    response.readEntity(String.class));
        }
    }

    @Test
    public void servletConfig() throws Exception {
        try (Response response = get("servletConfig")) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals(RootApplication.class.getName(), response.readEntity(String.class));
        }
    }

    protected Response get(final String path) throws URISyntaxException {
        return client.target(TestUtil.generateUri(url, resourcePath + "/" + path))
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
