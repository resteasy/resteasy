/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.InvalidAbstractCdiBeanContactResource;
import org.jboss.resteasy.test.cdi.injection.resource.InvalidCdiBeanContactResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that a resource which CDI cannot register as a bean is still deployed and created by RESTEasy. See
 * RESTEASY-3752.
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.7.Final")
public class InvalidCdiBeanResourceTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, InvalidCdiBeanResourceTest.class.getSimpleName() + ".war")
                .addClasses(InvalidCdiBeanContactResource.class, RootApplication.class,
                        // We include this, but never attempt to invoke it. It should result in a deployment failure
                        // if it is processed as a CDI bean.
                        InvalidAbstractCdiBeanContactResource.class)
                .addAsWebInfResource(TestUtil.createBeansXml("all"), "beans.xml");
    }

    @Test
    public void invalidBeanResourceDeployed() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            Assertions.assertEquals("ID: 1", client.target(TestUtil.generateUri(baseUri, "contact/1/details"))
                    .request()
                    .get(String.class));
        }
    }
}
