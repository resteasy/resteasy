/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class ClientFactoryTest {

    /**
     * Checks that the clients {@link org.jboss.resteasy.client.jaxrs.ClientHttpEngine} is a {@link JavaClientHttpEngine}
     */
    @Test
    void checkJavaClientHttpEngine() {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Assertions.assertInstanceOf(JavaClientHttpEngine.class, client.httpEngine());
        }
    }
}
