/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2026 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.undertow;

import static org.jboss.resteasy.test.undertow.TestSupport.generateURL;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * A {@link jakarta.ws.rs.container.TimeoutHandler} may extend the timeout by calling
 * {@link AsyncResponse#setTimeout(long, TimeUnit)} instead of resuming. The watchdog the
 * handler re-arms must survive the timeout dispatch, and the second timeout must fire.
 *
 * @author <a href="mailto:stevenschlansker@gmail.com">Steven Schlansker</a>
 */
public class AsyncTimeoutExtensionTest {

    static Client client;
    static UndertowJaxrsServer server;

    @ApplicationPath("/")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TimeoutExtensionResource.class);
        }
    }

    @Path("timeout-extension")
    public static class TimeoutExtensionResource {
        private static final AtomicBoolean EXTENDED = new AtomicBoolean(false);

        @GET
        @Path("extended")
        public void extendedTimeout(@Suspended final AsyncResponse response) {
            EXTENDED.set(false);
            response.setTimeoutHandler(ar -> {
                if (EXTENDED.getAndSet(true)) {
                    ar.resume("extended");
                } else {
                    ar.setTimeout(1, TimeUnit.SECONDS);
                }
            });
            response.setTimeout(1, TimeUnit.SECONDS);
        }
    }

    @BeforeAll
    public static void init() throws Exception {
        server = new UndertowJaxrsServer().start();
        server.deploy(MyApp.class);
        // A broken re-arm hangs the request forever; the read timeout turns that into a
        // bounded failure instead of a hung build.
        client = ClientBuilder.newBuilder().readTimeout(15, TimeUnit.SECONDS).build();
    }

    @AfterAll
    public static void stop() throws Exception {
        try {
            client.close();
        } catch (Exception ignored) {
        }
        server.stop();
    }

    @Test
    public void timeoutHandlerExtendsTheTimeout() {
        final String val = client.target(generateURL("/timeout-extension/extended"))
                .request()
                .get(String.class);
        Assertions.assertEquals("extended", val);
    }
}
