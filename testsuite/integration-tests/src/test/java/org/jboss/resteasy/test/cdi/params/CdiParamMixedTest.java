/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import java.net.URI;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.params.resources.CdiParamMixedResource;
import org.jboss.resteasy.test.cdi.params.resources.ParamApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that CDI injection works on a constructor with both {@code @*Param} annotations and standard CDI beans.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.3")
@Tag("requires-enhanced-cdi")
class CdiParamMixedTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, CdiParamMixedTest.class.getSimpleName() + ".war")
                .addClasses(ParamApplication.class, CdiParamMixedResource.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    void mixedConstructor() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("mixed")
                    .queryParam("q", "resteasy");
            final JsonObject result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Custom", "custom-value")
                    .get(JsonObject.class);
            Assertions.assertNotNull(result);
            Assertions.assertEquals("resteasy", result.getString("query"),
                    () -> "@QueryParam should be injected in mixed constructor: %s".formatted(result));
            Assertions.assertFalse(result.getString("path").isEmpty(),
                    () -> "UriInfo should be injected in mixed constructor: %s".formatted(result));
            Assertions.assertEquals("custom-value", result.getString("customHeader"),
                    () -> "@HeaderParam should be injected in mixed constructor: %s".formatted(result));
        }
    }
}
