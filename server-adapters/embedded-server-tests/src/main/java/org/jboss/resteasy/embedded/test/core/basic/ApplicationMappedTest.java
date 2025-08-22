/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.basic;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

import jakarta.inject.Inject;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;

import org.jboss.jandex.Index;
import org.jboss.resteasy.core.se.ConfigurationOption;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceB;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(value = ApplicationMappedTest.ApplicationTestMappedApplication.class, configFactory = ApplicationMappedTest.TestConfigurationProvider.class)
public class ApplicationMappedTest {

    private static final String CONTENT_ERROR_MESSAGE = "Wrong content of response";

    @Inject
    private Client client;

    /**
     * @tpTestDetails Test scanned application in deployment: No declared ApplicationPath,
     *                no declared getClasses and getSingletons methods. This application is mapped
     *                to different location using setRootResourcePath. This replaces the web.xml
     *                statement <url-pattern>/mapped/*</url-pattern> in <servlet-mapping>.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    @Inject
    public void testMapped(final URI uri) {

        WebTarget base = client.target(uri);

        String value = base.path("resources/a").request().get(String.class);
        Assertions.assertEquals("a", value, CONTENT_ERROR_MESSAGE);

        value = base.path("resources/b").request().get(String.class);
        Assertions.assertEquals("b", value, CONTENT_ERROR_MESSAGE);

        value = base.path("singletons/a").request().get(String.class);
        Assertions.assertEquals("a", value, CONTENT_ERROR_MESSAGE);

        value = base.path("singletons/b").request().get(String.class);
        Assertions.assertEquals("b", value, CONTENT_ERROR_MESSAGE);
    }

    public static class ApplicationTestMappedApplication extends Application {
    }

    public static class TestConfigurationProvider implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration() {
            final Index index;
            try {
                index = Index.of(ApplicationTestResourceA.class,
                        ApplicationTestResourceB.class,
                        ApplicationTestSingletonA.class,
                        ApplicationTestSingletonB.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return SeBootstrap.Configuration.builder()
                    .property(ConfigurationOption.JANDEX_INDEX.key(), index)
                    .rootPath("/mapped")
                    .build();
        }
    }
}
