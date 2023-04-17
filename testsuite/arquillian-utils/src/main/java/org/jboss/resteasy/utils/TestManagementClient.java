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

package org.jboss.resteasy.utils;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.as.controller.client.helpers.DelegatingModelControllerClient;

/**
 * A simple {@linkplain ModelControllerClient management client} which exposes the configuration used to create the
 * client.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestManagementClient extends DelegatingModelControllerClient {
    private final ModelControllerClientConfiguration config;

    private TestManagementClient(final ModelControllerClientConfiguration config) {
        super(ModelControllerClient.Factory.create(config));
        this.config = config;
    }

    /**
     * Creates a new management client with the default {@linkplain TestUtil#getManagementHost() host} and
     * {@linkplain TestUtil#getManagementPort() port}.
     *
     * @return a new management client
     */
    public static TestManagementClient create() {
        return new TestManagementClient(createDefaultConfig());
    }

    /**
     * Creates a new management client with the host and port provided.
     *
     * @param hostName the host name
     * @param port     the port
     *
     * @return the new management client
     */
    public static TestManagementClient create(final String hostName, final int port) {
        return new TestManagementClient(
                new ModelControllerClientConfiguration.Builder()
                        .setHostName(hostName)
                        .setPort(port)
                        .build());
    }

    /**
     * Gets the configuration used to create the client.
     *
     * @return the configuration
     */
    public ModelControllerClientConfiguration getConfiguration() {
        return config;
    }

    /**
     * Creates the default management client configuration.
     *
     * @return the default config
     */
    static ModelControllerClientConfiguration createDefaultConfig() {
        return new ModelControllerClientConfiguration.Builder()
                .setHostName(TestUtil.getManagementHost())
                .setPort(TestUtil.getManagementPort())
                .build();
    }
}
