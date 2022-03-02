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
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.RequiredInjectableContextResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
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

    @BeforeClass
    public static void initClient() {
        client = ClientBuilder.newBuilder()
                .build();
    }

    @AfterClass
    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void application() throws Exception {
        final Response response = get("application/test.property");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("test value", response.readEntity(String.class));
    }

    @Test
    public void client() throws Exception {
        final Response response = get("client/request");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void configuration() throws Exception {
        final Response response = get("configuration");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals(RuntimeType.SERVER.name(), response.readEntity(String.class));
    }

    @Test
    public void httpHeader() throws Exception {
        final Response response = get("httpHeaders/test-header");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("test-value", response.readEntity(String.class));
    }

    @Test
    public void httpRequest() throws Exception {
        final Response response = get("httpRequest");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void provider() throws Exception {
        final Response response = get("providers");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        final String value = response.readEntity(String.class);
        Assert.assertTrue(String.format("Value expected to contain %s but was %s", TestExceptionMapper.class.getSimpleName(), value)
                , value.contains(TestExceptionMapper.class.getSimpleName()));
    }

    @Test
    public void request() throws Exception {
        final Response response = get("request");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void resourceContext() throws Exception {
        final Response response = get("resourceContext");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertTrue(response.readEntity(String.class)
                .startsWith(RequiredInjectableContextResource.class.getCanonicalName()));
    }

    @Test
    public void securityContext() throws Exception {
        final Response response = get("securityContext");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("false", response.readEntity(String.class));
    }

    @Test
    public void uriInfo() throws Exception {
        final Response response = get("uriInfo");
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assert.assertEquals("/inject/uriInfo", response.readEntity(String.class));
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
        Assert.assertEquals("test", cf.get(5, TimeUnit.SECONDS));
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
