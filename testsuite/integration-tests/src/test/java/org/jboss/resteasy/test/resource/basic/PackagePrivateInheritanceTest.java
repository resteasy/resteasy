/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.PackagePrivateInheritanceResource;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-3621: a public resource class that inherits a
 *                    JAX-RS resource method from a package-private superclass must be invocable
 *                    without throwing IllegalAccessException.
 * @tpSince RESTEasy 6.2.17
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-core-spi", minVersion = "6.2.17.Final")
public class PackagePrivateInheritanceTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PackagePrivateInheritanceTest.class.getSimpleName());
        war.addClass(PackagePrivateInheritanceResource.class.getSuperclass());
        return TestUtil.finishContainerPrepare(war, null, PackagePrivateInheritanceResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PackagePrivateInheritanceTest.class.getSimpleName());
    }

    @BeforeAll
    public static void beforeAll() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void afterAll() {
        client.close();
    }

    /**
     * @tpTestDetails Verify that a GET request to a resource method declared in a package-private
     *                superclass succeeds with HTTP 200 and the expected response body.
     * @tpSince RESTEasy 6.2.17
     */
    @Test
    public void inheritedMethodFromPackagePrivateClassIsInvocable() {
        Response response = client.target(generateURL("/package-private-inheritance")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class));
    }
}
