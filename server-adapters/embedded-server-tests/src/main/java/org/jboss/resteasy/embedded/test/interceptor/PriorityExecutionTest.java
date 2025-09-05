/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.interceptor;

import java.net.URI;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpSince RESTEasy 4.1.0
 * @tpTestCaseDetails Regression test for RESTEASY-1294
 */
@RestBootstrap(PriorityExecutionTest.PriorityApplication.class)
public class PriorityExecutionTest {
    public static volatile Queue<String> interceptors = new ConcurrentLinkedQueue<>();
    public static Logger logger = Logger.getLogger(PriorityExecutionTest.class);
    private static final String WRONG_ORDER_ERROR_MSG = "Wrong order of interceptor execution";

    public static class PriorityApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                    PriorityExecutionResource.class,
                    PriorityExecutionContainerResponseFilter2.class,
                    PriorityExecutionContainerResponseFilter1.class,
                    PriorityExecutionContainerResponseFilter3.class,
                    PriorityExecutionContainerResponseFilterMin.class,
                    PriorityExecutionContainerResponseFilterMax.class,
                    PriorityExecutionContainerRequestFilter2.class,
                    PriorityExecutionContainerRequestFilter1.class,
                    PriorityExecutionContainerRequestFilter3.class,
                    PriorityExecutionContainerRequestFilterMin.class,
                    PriorityExecutionContainerRequestFilterMax.class);
        }
    }

    /**
     * @tpTestDetails Check order of client and server filters
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void testPriority(final Client client, @RequestPath("test") URI uri) throws Exception {
        client.register(PriorityExecutionClientResponseFilter3.class);
        client.register(PriorityExecutionClientResponseFilter1.class);
        client.register(PriorityExecutionClientResponseFilter2.class);
        client.register(PriorityExecutionClientResponseFilterMin.class);
        client.register(PriorityExecutionClientResponseFilterMax.class);
        client.register(PriorityExecutionClientRequestFilter3.class);
        client.register(PriorityExecutionClientRequestFilter1.class);
        client.register(PriorityExecutionClientRequestFilter2.class);
        client.register(PriorityExecutionClientRequestFilterMin.class);
        client.register(PriorityExecutionClientRequestFilterMax.class);

        Response response = client.target(uri).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("test", response.getEntity(), "Wrong content of response");

        // client filters
        Assertions.assertEquals("PriorityExecutionClientRequestFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);

        // server filters
        Assertions.assertEquals("PriorityExecutionContainerRequestFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);

        // client filters
        Assertions.assertEquals("PriorityExecutionClientResponseFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
    }
}
