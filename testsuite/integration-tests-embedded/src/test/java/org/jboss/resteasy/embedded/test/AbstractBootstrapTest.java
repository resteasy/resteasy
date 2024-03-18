/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.embedded.test;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.core.se.ConfigurationOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class AbstractBootstrapTest {

    protected Client client;
    protected SeBootstrap.Instance instance;

    @BeforeEach
    public void createClient() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void shutdown() throws Exception {
        if (client != null) {
            client.close();
        }
        if (instance != null) {
            instance.stop().toCompletableFuture().get(60, TimeUnit.SECONDS);
        }
    }

    protected void start(final Application application)
            throws ExecutionException, InterruptedException, TimeoutException {
        start(application, SeBootstrap.Configuration.builder()
                .property(ConfigurationOption.JANDEX_CLASS_PATH_FILTER.key(), (Predicate<Path>) (path) -> path.getFileName()
                        .toString()
                        .endsWith(".class"))
                .build());
    }

    protected void start(final Application application, final SeBootstrap.Configuration configuration)
            throws ExecutionException, InterruptedException, TimeoutException {
        instance = SeBootstrap.start(application, configuration)
                .toCompletableFuture()
                .get(60, TimeUnit.SECONDS);
    }
}
