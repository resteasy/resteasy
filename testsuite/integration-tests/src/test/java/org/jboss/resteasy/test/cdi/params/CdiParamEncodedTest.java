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
import org.jboss.resteasy.test.cdi.params.resources.CdiParamEncodedResource;
import org.jboss.resteasy.test.cdi.params.resources.CdiParamNotEncodedResource;
import org.jboss.resteasy.test.cdi.params.resources.EncodedDescriptor;
import org.jboss.resteasy.test.cdi.params.resources.ParamApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that CDI injected parameters work with {@link jakarta.ws.rs.Encoded @Encoded}
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.3")
@Tag("requires-enhanced-cdi")
class CdiParamEncodedTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, CdiParamEncodedTest.class.getSimpleName() + ".war")
                .addClasses(ParamApplication.class, CdiParamEncodedResource.class,
                        CdiParamNotEncodedResource.class, EncodedDescriptor.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    void classLevelEncoded() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("encoded/hello%20world")
                    .queryParam("q", "hello%20world");
            final EncodedDescriptor result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(EncodedDescriptor.class);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.getQueryValue().contains("%20"),
                    () -> "Class-level @Encoded query should retain %%20, got: %s".formatted(result.getQueryValue()));
            Assertions.assertTrue(result.getPathValue().contains("%20"),
                    () -> "Class-level @Encoded path should retain %%20, got: %s".formatted(result.getPathValue()));
        }
    }

    @Test
    void fieldLevelEncoded() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("not-encoded/hello%20world")
                    .queryParam("q", "hello%20world")
                    .queryParam("q2", "hello%20world");
            final EncodedDescriptor result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(EncodedDescriptor.class);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.getQueryValue().contains("%20"),
                    () -> "Field-level @Encoded query should retain %%20, got: %s".formatted(result.getQueryValue()));
            Assertions.assertEquals("hello world", result.getDecodedQuery(),
                    () -> "non-encoded query should decode %%20 to space: %s".formatted(result));
            Assertions.assertEquals("hello world", result.getPathValue(),
                    () -> "non-encoded path should decode %%20 to space: %s".formatted(result));
        }
    }
}
