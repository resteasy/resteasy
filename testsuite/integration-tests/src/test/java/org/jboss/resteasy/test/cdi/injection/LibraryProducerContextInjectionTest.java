/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import java.net.URL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.LibraryProducer;
import org.jboss.resteasy.test.cdi.injection.resource.LibraryProducerResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that CDI producers for servlet context types (specifically HttpServletResponse) and Client
 * defined in an external library (WEB-INF/lib) do not conflict with RESTEasy's synthetic fallback
 * beans (RESTEASY-3820).
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class LibraryProducerContextInjectionTest {

    private static Client client;

    @ArquillianResource
    private URL url;

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        if (client != null) {
            client.close();
        }
    }

    @Deployment
    public static Archive<?> deployment() {
        final JavaArchive libraryJar = ShrinkWrap.create(JavaArchive.class, "library-producer.jar")
                .addClasses(LibraryProducer.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        return ShrinkWrap.create(WebArchive.class, LibraryProducerContextInjectionTest.class.getSimpleName() + ".war")
                .addClasses(RootApplication.class, LibraryProducerResource.class)
                .addAsLibrary(libraryJar)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    private Response get(final String path) throws Exception {
        return client.target(TestUtil.generateUri(url, "library-test/" + path))
                .request()
                .get();
    }

    @Test
    public void testInjectedResponse() throws Exception {
        try (Response response = get("response")) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals("library-response", response.readEntity(String.class));
        }
    }

    @Test
    public void testInjectedClient() throws Exception {
        try (Response response = get("client")) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals("library-client", response.readEntity(String.class));
        }
    }

    @Test
    public void testContextResponse() throws Exception {
        try (Response response = get("context-response")) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals("ok", response.readEntity(String.class));
        }
    }
}
