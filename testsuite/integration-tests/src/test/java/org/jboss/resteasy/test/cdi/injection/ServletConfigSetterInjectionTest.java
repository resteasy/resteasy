/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.ServletConfigOtherServlet;
import org.jboss.resteasy.test.cdi.injection.resource.ServletConfigSetterResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that the parameter of a {@code @Context} setter method which is also annotated with {@code @Inject} is
 * {@code @Context} qualified. The deployment contains another servlet which is a CDI bean, so an unqualified
 * {@code ServletConfig} would not be the {@code ServletConfig} of the Jakarta REST application.
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "8.0.0.Alpha1")
public class ServletConfigSetterInjectionTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, ServletConfigSetterInjectionTest.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, ServletConfigOtherServlet.class, ServletConfigSetterResource.class);
    }

    @Test
    public void injectContextSetter() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(baseUri, "servlet-config")).request().get()) {
            Assertions.assertEquals(200, response.getStatus(), () -> response.readEntity(String.class));
            Assertions.assertEquals(TestApplication.class.getName(), response.readEntity(String.class));
        }
    }
}
