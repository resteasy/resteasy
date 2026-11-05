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

package org.jboss.resteasy.test.providers.disabled;

import java.net.URI;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.test.providers.disabled.resources.DisabledProvidersApplication;
import org.jboss.resteasy.test.providers.disabled.resources.DisabledProvidersResource;
import org.jboss.resteasy.test.providers.disabled.resources.SimpleText;
import org.jboss.resteasy.test.providers.disabled.resources.SimpleTextReaderWriter;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * @tpSubChapter Disabled providers: context parameter "resteasy.disable.providers" is used to disable providers.
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-1510
 * @tpSince RESTEasy 3.10.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(LoggingSetupTask.class)
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.0.Alpha2")
public class DisabledProvidersTest {

    static Client client;

    @ArquillianResource
    @OperateOnDeployment("enabled")
    private URI enabledUri;

    @ArquillianResource
    @OperateOnDeployment("disabledApplicationClassProviders")
    private URI disabledApplicationClassProvidersUri;

    @ArquillianResource
    @OperateOnDeployment("disabledScannedProviders")
    private URI disabledScannedProvidersUri;

    @ArquillianResource
    @OperateOnDeployment("disabledConfiguredProviders")
    private URI disabledConfiguredProvidersUri;

    @Deployment(name = "enabled")
    public static WebArchive enabled() {
        return createDeployment("_enabled");
    }

    @Deployment(name = "disabledApplicationClassProviders")
    public static WebArchive actualProviders() {
        return createDeployment("_disabled_application_class_providers", DisabledProvidersApplication.class)
                .addAsWebInfResource(TestUtil.createWebXml(null, null, Map.of(
                        "resteasy.disable.providers",
                        "org.jboss.resteasy.plugins.providers.StringTextStar,org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider,org.jboss.resteasy.test.providers.disabled.resources.SimpleTextReaderWriter")),
                        "web.xml");
    }

    @Deployment(name = "disabledScannedProviders")
    public static WebArchive scannedProviders() {
        return createDeployment("_disabled_scanned_providers")
                .addAsWebInfResource(TestUtil.createWebXml(null, null, Map.of(
                        "resteasy.disable.providers",
                        "org.jboss.resteasy.plugins.providers.StringTextStar,org.jboss.resteasy.test.providers.disabled.resources.SimpleTextReaderWriter")),
                        "web.xml");
    }

    @Deployment(name = "disabledConfiguredProviders")
    public static WebArchive configuredProviders() {
        return createDeployment("_disabled_configured_providers")
                .addAsWebInfResource(TestUtil.createWebXml(null, null, Map.of(
                        "resteasy.disable.providers",
                        "org.jboss.resteasy.plugins.providers.DefaultTextPlain,org.jboss.resteasy.test.providers.disabled.resources.SimpleTextReaderWriter",
                        "resteasy.providers",
                        "org.jboss.resteasy.plugins.providers.DefaultTextPlain,org.jboss.resteasy.test.providers.disabled.resources.SimpleTextReaderWriter")),
                        "web.xml");
    }

    private static WebArchive createDeployment(final String suffix) {
        return createDeployment(suffix, TestApplication.class);
    }

    private static WebArchive createDeployment(final String suffix, final Class<? extends Application> application) {
        return ShrinkWrap.create(WebArchive.class, DisabledProvidersTest.class.getSimpleName() + suffix + ".war")
                .addClasses(SimpleText.class, SimpleTextReaderWriter.class, DisabledProvidersResource.class, application);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
        client.register(SimpleTextReaderWriter.class);
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1510
     *                Nothing is disabled in this case.
     * @tpSince RESTEasy 3.10.0
     */
    @Test
    public void testEnabled() throws Exception {
        try (Response response = client.target(TestUtil.generateUri(enabledUri, "/echo/testEnabled")).request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            final SimpleText simpleText = response.readEntity(SimpleText.class);
            Assertions.assertEquals("testEnabled", simpleText.getText());
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1510
     *
     *                In deployment "disabledApplicationClassProviders", providers are derived
     *                in ResteasyDeployment from Application.getClasses().
     * @tpSince RESTEasy 3.10.0
     */
    @Test
    public void testApplicationClassProvidersDisabled() throws Exception {
        try (
                Response response = client
                        .target(TestUtil.generateUri(disabledApplicationClassProvidersUri,
                                "/echo/testApplicationClassProvidersDisabled"))
                        .request()
                        .accept("application/simple-text")
                        .get()) {
            Assertions.assertEquals(500, response.getStatus(), () -> String.format("Expected a status of 500 got %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String message = response.readEntity(String.class);
            Assertions.assertTrue(message.contains("Could not find MessageBodyWriter"));
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1510
     *
     *                In deployment "disabledScannedProviders", Application.getClasses() and Application.getSingletons()
     *                return empty sets, and so wildfly-jaxrs scans available classes looking for providers. The names of
     *                those providers are passed to RESTEasy in the web context parameter "resteasy.scanned.providers".
     * @tpSince RESTEasy 3.10.0
     */
    @Test
    public void testScannedProvidersDisabled() throws Exception {
        try (
                Response response = client
                        .target(TestUtil.generateUri(disabledScannedProvidersUri, "/echo/testScannedProvidersDisabled"))
                        .request()
                        .get()) {
            Assertions.assertEquals(500, response.getStatus(), () -> String.format("Expected a status of 500 got %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String message = response.readEntity(String.class);
            Assertions.assertTrue(message.contains("Could not find MessageBodyWriter"));
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1510
     *
     *                In deployment "disabledConfiguredProviders", a list of providers is passed to RESTEasy
     *                in the web context parameter "resteasy.providers".
     * @tpSince RESTEasy 3.10.0
     */
    @Test
    public void testConfiguredProvidersDisabled() throws Exception {
        try (
                Response response = client
                        .target(TestUtil.generateUri(disabledConfiguredProvidersUri, "/echo/testConfiguredProvidersDisabled"))
                        .request()
                        .get()) {
            Assertions.assertEquals(500, response.getStatus(), () -> String.format("Expected a status of 500 got %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String message = response.readEntity(String.class);
            Assertions.assertTrue(message.contains("Could not find MessageBodyWriter"));
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1510
     *
     *                This test uses deployment "disabledConfiguredProviders", in which web.xml has uses context parameter
     *                "resteasy.disable.providers" to disable builtin provider StringTextStar.
     * @tpSince RESTEasy 3.10.0
     */
    @Test
    public void testBuiltinProviderDisabled() throws Exception {
        try (
                Response response = client.target(TestUtil.generateUri(disabledApplicationClassProvidersUri, "/string"))
                        .request()
                        .get()) {
            Assertions.assertEquals(500, response.getStatus(), () -> String.format("Expected a status of 500 got %s: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String message = response.readEntity(String.class);
            Assertions.assertTrue(message.contains("Could not find MessageBodyWriter"));
        }
    }
}
