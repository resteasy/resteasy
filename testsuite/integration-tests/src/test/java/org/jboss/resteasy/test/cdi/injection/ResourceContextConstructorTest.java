/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection;

import java.net.URI;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.ConstructorInjectedResource;
import org.jboss.resteasy.test.cdi.injection.resource.LookupResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that {@link ResourceContext#getResource(Class)} creates a CDI bean which only has an {@link Inject @Inject}
 * constructor with a parameter that is not annotated with {@link Context @Context}.
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.6.Final")
public class ResourceContextConstructorTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, ResourceContextConstructorTest.class.getSimpleName() + ".war")
                .addClasses(LookupResource.class, ConstructorInjectedResource.class, RootApplication.class)
                .addAsWebInfResource(TestUtil.createBeansXml("all"), "beans.xml");
    }

    @Test
    public void constructorInjected() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            Assertions.assertEquals("/lookup", client.target(TestUtil.generateUri(baseUri, "lookup")).request()
                    .get(String.class));
        }
    }
}
