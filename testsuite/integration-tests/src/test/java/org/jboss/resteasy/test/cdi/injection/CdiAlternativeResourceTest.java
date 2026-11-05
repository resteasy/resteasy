/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection;

import java.net.URI;

import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.AlternativeOrderResource;
import org.jboss.resteasy.test.cdi.injection.resource.OrderResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that an enabled {@link Alternative @Alternative} for a CDI managed resource is the bean used to handle the
 * request.
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "6.2.20.Final")
public class CdiAlternativeResourceTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, CdiAlternativeResourceTest.class.getSimpleName() + ".war")
                .addClasses(OrderResource.class, AlternativeOrderResource.class, RootApplication.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    public void alternativeResourceInvoked() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            Assertions.assertEquals("alternative", client.target(TestUtil.generateUri(baseUri, "orders")).request()
                    .get(String.class));
        }
    }
}
