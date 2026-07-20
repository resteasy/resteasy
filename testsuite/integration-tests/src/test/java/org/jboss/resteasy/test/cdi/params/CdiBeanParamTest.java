/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.params.resources.CdiBeanParamResource;
import org.jboss.resteasy.test.cdi.params.resources.ParamApplication;
import org.jboss.resteasy.test.cdi.params.resources.SearchParams;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that a {@link jakarta.ws.rs.BeanParam @BeanParam} works with CDI.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.3")
@Tag("requires-enhanced-cdi")
class CdiBeanParamTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, CdiBeanParamTest.class.getSimpleName() + ".war")
                .addClasses(ParamApplication.class, CdiBeanParamResource.class, SearchParams.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    void beanParam() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("bean-param")
                    .queryParam("q", "resteasy")
                    .queryParam("limit", 25);
            final SearchParams result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Accept-Language", "en-US")
                    .get(SearchParams.class);
            Assertions.assertNotNull(result);
            Assertions.assertEquals("resteasy", result.getQuery(),
                    () -> "Expected a result of 'resteasy' in the query parameter: %s".formatted(result));
            Assertions.assertEquals(25, result.getLimit(),
                    () -> "Expected a result of '25' in the limit parameter: %s".formatted(result));
            Assertions.assertEquals("en-US", result.getLanguage(),
                    () -> "Expected a result of 'en-US' in the Accepted-Language parameter: %s".formatted(result));
        }
    }

    @Test
    void beanParamDefaults() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("bean-param")
                    .queryParam("q", "test");
            final SearchParams result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(SearchParams.class);
            Assertions.assertNotNull(result);
            Assertions.assertEquals("test", result.getQuery(),
                    () -> "Expected a result of 'test' in the query parameter: %s".formatted(result));
            Assertions.assertEquals(10, result.getLimit(),
                    () -> "Expected a result of '10', @DefaultValue, in the limit parameter: %s".formatted(result));
            Assertions.assertNull(result.getLanguage(), () -> "Accept-Language header should be null: %s".formatted(result));
        }
    }
}
