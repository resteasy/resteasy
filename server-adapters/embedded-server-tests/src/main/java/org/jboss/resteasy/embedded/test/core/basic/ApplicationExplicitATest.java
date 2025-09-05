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

import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonA;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(value = ApplicationExplicitATest.ApplicationTestAExplicitApplication.class)
public class ApplicationExplicitATest {

    @Inject
    private Client client;

    /**
     * @tpTestDetails Test first application definition. Declared ApplicationPath,
     *                getClasses and getSingletons methods
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    @Inject
    public void testExplicitA(@RequestPath("/a/explicit") final URI uri) {

        WebTarget base = client.target(uri);

        String value = base.path("resources/a").request().get(String.class);
        Assertions.assertEquals("a", value);

        Response response = base.path("resources/b").request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());

        value = base.path("singletons/a").request().get(String.class);
        Assertions.assertEquals("a", value);

        response = base.path("singletons/b").request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
    }

    @ApplicationPath("a/explicit")
    public static class ApplicationTestAExplicitApplication extends Application {

        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ApplicationTestResourceA.class);
        }

        @Override
        public Set<Object> getSingletons() {
            return Set.of(new ApplicationTestSingletonA());
        }
    }
}
