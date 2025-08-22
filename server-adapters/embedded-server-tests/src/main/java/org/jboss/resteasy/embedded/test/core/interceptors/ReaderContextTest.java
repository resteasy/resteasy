/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.interceptors;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextArrayListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstReaderInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstWriterInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextLinkedListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextResource;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextSecondReaderInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Basic test for reated context
 * @tpSince RESTEasy 4.1.0
 */
@RestBootstrap(ReaderContextTest.TestApplication.class)
public class ReaderContextTest {

    @Inject
    private Client client;

    /**
     * @tpTestDetails Check post request.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void readerContextOnClientTest(@RequestPath("/resource/poststring") final URI uri) {
        client = ClientBuilder.newClient();

        WebTarget target = client.target(uri);
        target.register(ReaderContextFirstReaderInterceptor.class);
        target.register(ReaderContextSecondReaderInterceptor.class);
        target.register(ReaderContextArrayListEntityProvider.class);
        target.register(ReaderContextLinkedListEntityProvider.class);
        try (Response response = target.request().post(Entity.text("plaintext"))) {
            response.getHeaders().add(ReaderContextResource.HEADERNAME,
                    ReaderContextFirstReaderInterceptor.class.getName());
            @SuppressWarnings("unchecked")
            List<String> list = response.readEntity(List.class);
            Assertions.assertInstanceOf(ArrayList.class, list, "Returned list in not instance of ArrayList");
            String entity = list.get(0);
            Assertions.assertTrue(entity.contains(ReaderContextSecondReaderInterceptor.class.getName()),
                    "Wrong interceptor type in response");
            Assertions.assertTrue(entity.contains(ReaderContextSecondReaderInterceptor.class.getAnnotations()[0]
                    .annotationType().getName()), "Wrong interceptor annotation in response");
        }
    }

    public static class TestApplication extends Application {

        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ReaderContextResource.class,
                    ReaderContextArrayListEntityProvider.class,
                    ReaderContextLinkedListEntityProvider.class,
                    ReaderContextFirstReaderInterceptor.class,
                    ReaderContextFirstWriterInterceptor.class,
                    ReaderContextSecondReaderInterceptor.class);
        }
    }
}
