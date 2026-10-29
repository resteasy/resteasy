/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.basic;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.cdi.basic.resource.DependentLifecycleTracker;
import org.jboss.resteasy.test.cdi.basic.resource.DependentLifecycleTrackerResource;
import org.jboss.resteasy.test.cdi.basic.resource.DependentResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that {@link jakarta.enterprise.context.Dependent} resources are destroyed once the request has finished.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "8.0.0.Alpha1")
public class DependentResourceLifecycleTest {
    private static Client client;

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        final WebArchive war = TestUtil.prepareArchive(DependentResourceLifecycleTest.class.getSimpleName())
                .addClass(DependentLifecycleTracker.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, DependentResource.class,
                DependentLifecycleTrackerResource.class);
    }

    @BeforeAll
    public static void createClient() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Tests that a {@link jakarta.enterprise.context.Dependent} resource is destroyed after a synchronous request.
     */
    @Test
    public void syncRequest() throws Exception {
        final int expected = destroyedCount() + 1;
        assertOk("dependent/sync", "sync");
        assertDestroyedCount(expected);
    }

    /**
     * Tests that a {@link jakarta.enterprise.context.Dependent} resource is destroyed after an asynchronous request
     * which is resumed on another thread.
     */
    @Test
    public void asyncResumeOnOtherThread() throws Exception {
        final int expected = destroyedCount() + 1;
        assertOk("dependent/async", "async");
        assertDestroyedCount(expected);
    }

    /**
     * Tests that a {@link jakarta.enterprise.context.Dependent} resource which resumes the asynchronous response on the
     * invoking thread is not destroyed until the resource method returns.
     */
    @Test
    public void asyncResumeOnInvokingThread() throws Exception {
        final int expected = destroyedCount() + 1;
        assertOk("dependent/async-invoking", "async-invoking");
        assertDestroyedCount(expected);
        try (Response response = client.target(generateURL("tracker/destroyed-before-return")).request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertFalse(response.readEntity(Boolean.class),
                    "The resource was destroyed before the resource method returned");
        }
    }

    private static void assertOk(final String path, final String expectedEntity) {
        try (Response response = client.target(generateURL(path)).request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(expectedEntity, response.readEntity(String.class));
        }
    }

    private static void assertDestroyedCount(final int expected) throws InterruptedException {
        // An asynchronous request may finish after the response has been received by the client
        final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        int count = destroyedCount();
        while (count < expected && System.nanoTime() < end) {
            TimeUnit.MILLISECONDS.sleep(100);
            count = destroyedCount();
        }
        Assertions.assertEquals(expected, count, "Expected the resource to be destroyed exactly once");
    }

    private static int destroyedCount() {
        try (Response response = client.target(generateURL("tracker/destroyed")).request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            return response.readEntity(Integer.class);
        }
    }

    private static String generateURL(final String path) {
        return PortProviderUtil.generateURL("/" + path, DependentResourceLifecycleTest.class.getSimpleName());
    }
}
