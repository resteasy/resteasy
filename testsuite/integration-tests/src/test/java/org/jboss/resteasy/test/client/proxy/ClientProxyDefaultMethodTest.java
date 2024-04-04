/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.client.proxy;

import java.net.URI;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.test.client.proxy.resource.ClientSmokeResource;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceSimpleClient;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Smoke tests for jaxrs
 * @tpChapter Integration tests
 * @tpTestCaseDetails Client proxy supports interface default methods
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(LoggingSetupTask.class)
public class ClientProxyDefaultMethodTest {

    @ArquillianResource
    private URI uri;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        return ShrinkWrap.create(WebArchive.class, "proxy-default-methods.war")
                .addClasses(ClientSmokeResource.class, ClientInvokingSmokeResource.class, TestApplication.class,
                        ResourceWithInterfaceSimpleClient.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * @tpTestDetails Check proxy supports interface default method
     */
    @Test
    public void testNoDefaultsResource() {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            ResourceWithInterfaceSimpleClient proxy = client.target(uri)
                    .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

            Assertions.assertEquals("basic", proxy.getBasicThroughDefaultMethod(), "Wrong client answer.");
        }
    }

    /**
     * Tests a default method passing a parameter
     */
    @Test
    public void defaultResourceParameter() {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            ResourceWithInterfaceSimpleClient proxy = client.target(uri)
                    .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

            final String response = proxy.defaultQueryParameter("queryParam");
            Assertions.assertEquals("queryParam", response);
        }
    }

    /**
     * Tests, in the container, that a proxy works. This is primarily to test with security manager permissions.
     * <p>
     * We are required to use the client on an internal resource to not give the deployment permissions Arquillian
     * needs which could hide issues in the implementation which would throw the same exceptions.
     * </p>
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void inContainerDefaultResourceParameter() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final String response = client.target(TestUtil.generateUri(uri, "/client"))
                    .queryParam("param", "queryParam")
                    .request()
                    .get(String.class);
            Assertions.assertEquals("queryParam", response);
        }
    }

    @Path("/client")
    @RequestScoped
    public static class ClientInvokingSmokeResource {
        @Inject
        private UriInfo uriInfo;

        @GET
        public String queryParam(@QueryParam("param") final String value) {
            try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
                ResourceWithInterfaceSimpleClient proxy = client.target(uriInfo.getBaseUriBuilder())
                        .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

                return proxy.defaultQueryParameter(value);
            }
        }
    }

}
