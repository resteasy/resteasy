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
import org.jboss.resteasy.test.cdi.injection.resource.InvalidCdiBeanContactResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests that a resource which CDI cannot register as a bean is still deployed and created by RESTEasy. See
 * RESTEASY-3752.
 */
@ArquillianTest
@RunAsClient
public class InvalidCdiBeanResourceTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, InvalidCdiBeanResourceTest.class.getSimpleName() + ".war")
                .addClasses(InvalidCdiBeanContactResource.class, RootApplication.class)
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
