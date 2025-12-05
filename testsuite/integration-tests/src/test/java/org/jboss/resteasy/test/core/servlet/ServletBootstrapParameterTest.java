/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.core.servlet;

import java.net.URI;
import java.net.URISyntaxException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.core.servlet.resource.ServletBootstrapParameterApplication;
import org.jboss.resteasy.test.core.servlet.resource.ServletBootstrapParameterResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Regression test for RESTEASY-3678.
 * <p>
 * Tests that {@code ServletBootstrap.getParameter()} correctly checks servlet init-params before servlet
 * context-params.
 * </p>
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.core", minVersion = "6.2.15.Final")
public class ServletBootstrapParameterTest {
    private static Client client;

    @ArquillianResource
    private URI baseUri;

    @Deployment
    public static WebArchive deploySimpleResource() {
        return ShrinkWrap.create(WebArchive.class, ServletBootstrapParameterTest.class.getSimpleName() + ".war")
                // We use a web.xml here as we need to register the jakarta.ws.rs.Application and the servlet for the
                // init-params
                .addAsWebInfResource(ServletBootstrapParameterTest.class.getPackage(), "ServletBootstrapParameterWeb.xml",
                        "web.xml")
                .addClasses(ServletBootstrapParameterResource.class, ServletBootstrapParameterApplication.class);
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * Verifies that when a parameter exists in both servlet init-param and context-param, the servlet init-param value
     * takes precedence.
     */
    @Test
    public void testSharedParameterPrecedence() throws Exception {
        final String value = client.target(generateUri("/bootstrap/shared-param")).request().get(String.class);
        Assertions.assertEquals("from-servlet", value,
                "Servlet init-param should take precedence over context-param");
    }

    /**
     * Verifies that a parameter only in context-param is still accessible.
     */
    @Test
    public void testContextOnlyParameter() throws Exception {
        final String value = client.target(generateUri("/bootstrap/context-only")).request().get(String.class);
        Assertions.assertEquals("context-value", value,
                "Context-param should be accessible when not overridden");
    }

    /**
     * Verifies that a parameter only in servlet init-param is accessible.
     */
    @Test
    public void testServletOnlyParameter() throws Exception {
        final String value = client.target(generateUri("/bootstrap/servlet-only")).request().get(String.class);
        Assertions.assertEquals("servlet-value", value,
                "Servlet init-param should be accessible");
    }

    private URI generateUri(final String path) throws URISyntaxException {
        return TestUtil.generateUri(baseUri, path);
    }
}
