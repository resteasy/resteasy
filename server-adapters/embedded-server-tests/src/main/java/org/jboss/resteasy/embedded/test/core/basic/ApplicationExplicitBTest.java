/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.basic;

import java.net.URI;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceB;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonB;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(value = ApplicationExplicitBTest.ApplicationTestBExplicitApplication.class)
public class ApplicationExplicitBTest {

    private static final String CONTENT_ERROR_MESSAGE = "Wrong content of response";

    @Inject
    private Client client;

    /**
     * @tpTestDetails Test second application definition. Declared ApplicationPath,
     *                getClasses and getSingletons methods.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    @Inject
    public void testExplicitB(@RequestPath("/b/explicit") final URI uri) {

        WebTarget base = client.target(uri);

        String value = base.path("resources/b").request().get(String.class);
        Assertions.assertEquals("b", value, CONTENT_ERROR_MESSAGE);

        Response response = base.path("resources/a").request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());

        value = base.path("singletons/b").request().get(String.class);
        Assertions.assertEquals("b", value, CONTENT_ERROR_MESSAGE);

        response = base.path("singletons/a").request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
    }

    @ApplicationPath("b/explicit")
    public static class ApplicationTestBExplicitApplication extends Application {

        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ApplicationTestResourceB.class);
        }

        @Override
        public Set<Object> getSingletons() {
            return Set.of(new ApplicationTestSingletonB());
        }
    }
}
