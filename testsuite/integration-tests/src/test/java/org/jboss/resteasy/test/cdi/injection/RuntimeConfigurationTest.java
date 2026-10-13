/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import java.net.URL;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.ClientConfigurationFilter;
import org.jboss.resteasy.test.cdi.injection.resource.ClientNameResolver;
import org.jboss.resteasy.test.cdi.injection.resource.RuntimeConfigurationApplication;
import org.jboss.resteasy.test.cdi.injection.resource.RuntimeConfigurationResource;
import org.jboss.resteasy.test.cdi.injection.resource.RuntimeTrace;
import org.jboss.resteasy.test.cdi.injection.resource.RuntimeTraceProvider;
import org.jboss.resteasy.test.cdi.injection.resource.ServerConfigurationFilter;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests that an injected {@link jakarta.ws.rs.core.Configuration} and {@link jakarta.ws.rs.ext.Providers} resolve, on
 * each provider method invocation, to those of the runtime invoking the provider. A client in the container sends an
 * entity to the server and each provider records the runtime it sees. The reader and writer are shared by the client
 * and the server.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
public class RuntimeConfigurationTest {
    private static final String CLIENT = "CLIENT:client:client";
    private static final String SERVER = "SERVER:none:none";

    private static Client client;

    @ArquillianResource
    private URL url;

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, RuntimeConfigurationTest.class.getSimpleName() + ".war")
                .addClasses(RuntimeConfigurationApplication.class,
                        RuntimeConfigurationResource.class,
                        RuntimeTrace.class,
                        RuntimeTraceProvider.class,
                        ClientNameResolver.class,
                        ClientConfigurationFilter.class,
                        ServerConfigurationFilter.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @BeforeAll
    public static void initClient() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * A synchronous client invocation.
     */
    @Test
    public void sync() throws Exception {
        assertTrace("runtime/sync");
    }

    /**
     * An asynchronous client invocation, where the client response filter and reader may run on another thread.
     */
    @Test
    public void async() throws Exception {
        assertTrace("runtime/async");
    }

    private void assertTrace(final String path) throws Exception {
        try (Response response = client.target(TestUtil.generateUri(url, path)).request(MediaType.TEXT_PLAIN).get()) {
            final String entity = response.readEntity(String.class);
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo(), entity);
            Assertions.assertEquals(List.of(
                    "writer=%s".formatted(CLIENT),
                    "reader=%s".formatted(SERVER),
                    "writer=%s".formatted(SERVER),
                    "reader=%s".formatted(CLIENT),
                    "%s=%s".formatted(ServerConfigurationFilter.HEADER, SERVER),
                    "%s=%s".formatted(ClientConfigurationFilter.HEADER, CLIENT)),
                    List.of(entity.split("\n")));
        }
    }
}
