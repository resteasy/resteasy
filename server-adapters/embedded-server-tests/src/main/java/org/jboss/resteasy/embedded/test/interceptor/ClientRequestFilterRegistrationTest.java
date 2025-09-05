/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.interceptor;

import java.net.URI;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.embedded.test.interceptor.resource.ClientRequestFilterImpl;
import org.jboss.resteasy.embedded.test.interceptor.resource.ClientResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Tests @Provider annotation on ClientRequestFilter
 * @tpSince RESTEasy 4.1.0
 */
@RestBootstrap(ClientRequestFilterRegistrationTest.TestApplication.class)
public class ClientRequestFilterRegistrationTest {

    @Inject
    private Client client;

    @Test
    public void filterRegisteredTest(@RequestPath("/testIt") final URI uri) throws Exception {
        WebTarget base = client.target(uri);
        Response response = base.request().get();
        Assertions.assertEquals(456, response.getStatus());
    }

    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ClientResource.class, ClientRequestFilterImpl.class);
        }
    }

}
