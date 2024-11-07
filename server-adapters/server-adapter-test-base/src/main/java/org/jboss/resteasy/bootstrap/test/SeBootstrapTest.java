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

package org.jboss.resteasy.bootstrap.test;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.SeBootstrap.Instance;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.jandex.Index;
import org.jboss.resteasy.core.se.ConfigurationOption;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class SeBootstrapTest {

    private Instance instance;

    @AfterEach
    public void shutdown() throws Exception {
        final Instance current = instance;
        instance = null;
        if (current != null) {
            final CountDownLatch latch = new CountDownLatch(1);
            current.stop()
                    .thenRun(latch::countDown);
            if (!latch.await(5, TimeUnit.SECONDS)) {
                Assertions.fail("Server did not shutdown within 5 seconds");
            }
        }
    }

    @Test
    public void unwrap() throws Exception {
        start();
        final EmbeddedServer server = instance.unwrap(getEmbeddedServerClass());
        Assertions.assertNotNull(server);
    }

    @Test
    public void constructedApplication() throws Exception {
        start(new DefaultApplication());
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(instance.configuration()
                    .baseUriBuilder()
                    .path("test/constructed"))
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("Hello constructed", response.readEntity(String.class));
        }
    }

    @Test
    public void bootWithApplication() throws Exception {
        start(SeBootstrapApplication1.class);
        final EmbeddedServer server = instance.unwrap(getEmbeddedServerClass());
        Assertions.assertNotNull(server);
        final Application application = server.getDeployment().getApplication();
        Assertions.assertTrue(
                application instanceof SeBootstrapApplication1,
                String.format("Expected %s but found %s", SeBootstrapApplication1.class, application));
    }

    @Test
    public void resourceDefined() throws Exception {
        start(SeBootstrapApplication1.class);
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(instance.configuration()
                    .baseUriBuilder()
                    .path("no-index/defined"))
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("Greetings defined", response.readEntity(String.class));

            response = client.target(instance.configuration()
                    .baseUriBuilder()
                    .path("setest1/test/skipped"))
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        }
    }

    @Test
    public void resourceScanned() throws Exception {
        start(SeBootstrapApplication2.class);
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(instance.configuration()
                    .baseUriBuilder()
                    .path("test/scanned"))
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("Hello scanned", response.readEntity(String.class));
        }
    }

    @Test
    public void scanningDisabled() throws Exception {
        start(NoScanAnnotation.class);
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(instance.configuration()
                    .baseUriBuilder()
                    .path("test/skipped"))
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        }
    }

    @Test
    @Tag("NoSslTests")
    public void httpsConnection() throws Exception {
        final Index index = Index.of(TestResource.class);
        start(new DefaultApplication(), SeBootstrap.Configuration.builder()
                .property(ConfigurationOption.JANDEX_INDEX.key(), index)
                .protocol("HTTPS")
                .sslContext(TestSslUtil.createServerSslContext())
                .build());
        try (Client client = ClientBuilder.newBuilder().sslContext(TestSslUtil.createClientSslContext()).build()) {
            final Response response = client.target(instance.configuration()
                    .baseUriBuilder()
                    .path("test/secure"))
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("Hello secure", response.readEntity(String.class));
        }
    }

    @Test
    public void overrideApplicationPath() throws Exception {
        final Index index = Index.of(TestResource.class);
        instance = SeBootstrap.start(DefaultApplication.class, SeBootstrap.Configuration.builder().rootPath("override")
                .property(ConfigurationOption.JANDEX_INDEX.key(), index).build())
                .toCompletableFuture().get(60, TimeUnit.SECONDS);
        try (Client client = ClientBuilder.newClient()) {
            final URI uri = instance.configuration()
                    .baseUriBuilder()
                    .path("test/constructed").build();
            final Response response = client.target(uri)
                    .request()
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo(), () -> String.format("Failure at %s", uri));
            Assertions.assertEquals("Hello constructed", response.readEntity(String.class));
        }

    }

    protected abstract Class<? extends EmbeddedServer> getEmbeddedServerClass();

    protected void start(final Class<? extends Application> application)
            throws ExecutionException, InterruptedException, TimeoutException, IOException {
        final Index index = Index.of(TestResource.class);
        final CompletionStage<Instance> cs = SeBootstrap.start(application, SeBootstrap.Configuration.builder()
                .property(ConfigurationOption.JANDEX_INDEX.key(), index)
                .build());
        instance = cs.toCompletableFuture().get(60, TimeUnit.MINUTES);
    }

    protected void start(final Application application)
            throws ExecutionException, InterruptedException, TimeoutException, IOException {
        final Index index = Index.of(TestResource.class);
        start(application, SeBootstrap.Configuration.builder()
                .property(ConfigurationOption.JANDEX_INDEX.key(), index)
                .build());
    }

    protected void start(final Application application, final SeBootstrap.Configuration configuration)
            throws ExecutionException, InterruptedException, TimeoutException, IOException {
        final CompletionStage<Instance> cs = SeBootstrap.start(application, configuration);
        instance = cs.toCompletableFuture().get(60, TimeUnit.MINUTES);
    }

    private void start() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        start(DefaultApplication.class);
    }

    @ApplicationPath("/default")
    public static class DefaultApplication extends Application {
    }

    @ApplicationPath("/setest1")
    @Priority(500)
    public static class SeBootstrapApplication1 extends Application {

        @Override
        public Set<Class<?>> getClasses() {
            return Collections.singleton(NonIndexedResource.class);
        }
    }

    @ApplicationPath("/setest2")
    @Priority(100)
    public static class SeBootstrapApplication2 extends Application {
    }

    @ApplicationPath("/noscan")
    public static class NoScanAnnotation extends Application {
        @Override
        public Map<String, Object> getProperties() {
            return Collections.singletonMap("jakarta.ws.rs.loadServices", Boolean.FALSE);
        }
    }

    @Path("/test")
    public static class TestResource {

        @GET
        @Path("/{name}")
        @Produces(MediaType.TEXT_PLAIN)
        public Response greet(@PathParam("name") final String name) {
            return Response.ok("Hello " + name).build();
        }
    }

    @Path("/no-index")
    public static class NonIndexedResource {

        @GET
        @Path("/{name}")
        @Produces(MediaType.TEXT_PLAIN)
        public Response greet(@PathParam("name") final String name) {
            return Response.ok("Greetings " + name).build();
        }
    }
}
