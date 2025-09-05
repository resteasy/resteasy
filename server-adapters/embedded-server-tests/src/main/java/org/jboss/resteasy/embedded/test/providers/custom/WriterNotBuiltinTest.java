/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.providers.custom;

import java.net.URI;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.se.ConfigurationOption;
import org.jboss.resteasy.embedded.test.providers.custom.resource.ReaderWriterResource;
import org.jboss.resteasy.embedded.test.providers.custom.resource.WriterNotBuiltinTestWriter;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Demonstrate MessageBodyWriter, MessageBodyReader
 * @tpSince RESTEasy 4.1.0
 */
@RestBootstrap(value = WriterNotBuiltinTest.TestApplication.class, configFactory = WriterNotBuiltinTest.TestConfiguration.class)
public class WriterNotBuiltinTest {

    @Inject
    private Client client;

    /**
     * @tpTestDetails TestReaderWriter has no type parameter,
     *                so it comes after DefaultPlainText in the built-in ordering.
     *                The fact that TestReaderWriter gets called verifies that
     *                DefaultPlainText gets passed over.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void test1New(@RequestPath("/string") final URI uri) throws Exception {
        Response response = client.target(uri).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        Assertions.assertEquals("hello world", response.readEntity(String.class), "Response contains wrong content");
        Assertions.assertTrue(WriterNotBuiltinTestWriter.used, "Wrong MessageBodyWriter was used");
    }

    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ReaderWriterResource.class, WriterNotBuiltinTestWriter.class);
        }
    }

    public static class TestConfiguration implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration() {
            return SeBootstrap.Configuration.builder()
                    .property(ConfigurationOption.REGISTER_BUILT_INS.key(), false)
                    .build();
        }
    }
}
