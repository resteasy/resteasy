/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.context;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.context.resource.TestApplication;
import org.jboss.resteasy.test.context.resource.TestResource;
import org.jboss.resteasy.test.context.resource.TestThreadContextProvider;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ThreadContextProviderTest {
    private final String asyncThreadName = "Test-Async-Client-Thread";
    private final AtomicLong expectedThreadId = new AtomicLong(-1L);

    private Client client;
    private ExecutorService testExecutorService;

    @ArquillianResource
    private URL url;

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, ThreadContextProviderTest.class.getSimpleName() + ".war")
                .addClasses(TestResource.class, TestApplication.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void initClient() {
        final ThreadFactory testThreadFactory = r -> {
            final Thread result = new Thread(r);
            result.setName(asyncThreadName);
            result.setDaemon(true);
            expectedThreadId.set(result.getId());
            return result;
        };
        testExecutorService = Executors.newSingleThreadExecutor(testThreadFactory);
        client = ClientBuilder.newBuilder()
                .executorService(testExecutorService)
                .build();
    }

    @After
    public void closeClient() {
        if (client != null) {
            client.close();
        }
        if (testExecutorService != null) {
            testExecutorService.shutdownNow();
        }
    }

    @Test
    public void clientThreadContext() throws Exception {
        final String currentThreadName = Thread.currentThread().getName();
        final CountDownLatch reset = new CountDownLatch(1);
        final TestThreadContextProvider threadContext = new TestThreadContextProvider(reset);
        final Future<Response> future = client.target(TestUtil.generateUri(url, "test"))
                .register(threadContext)
                .request()
                .async()
                .get();
        final Response response = future.get(5, TimeUnit.SECONDS);
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

        // Wait for the reset to be invoked
        Assert.assertTrue("Timeout waiting for remove to be invoked.", reset.await(5, TimeUnit.SECONDS));
        // Test context was propagated
        final Map<String, String> data = TestThreadContextProvider.localState.get();
        Assert.assertEquals(3, data.size());
        Assert.assertEquals(currentThreadName, data.get("captured"));
        Assert.assertEquals(asyncThreadName, data.get("push"));
        Assert.assertEquals(asyncThreadName, data.get("reset"));
    }
}
