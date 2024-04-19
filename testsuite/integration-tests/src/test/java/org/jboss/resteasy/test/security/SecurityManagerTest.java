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

package org.jboss.resteasy.test.security;

import java.net.URI;
import java.util.Map;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.setup.SystemPropertySetupTask;
import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.spi.config.security.ConfigPropertyPermission;
import org.jboss.resteasy.test.security.resource.RestActivator;
import org.jboss.resteasy.test.security.resource.SecurityCheckResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * Tests that a deployment cannot exploit privileged actions.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup({ LoggingSetupTask.class, SecurityManagerTest.ConfigureSetupTask.class })
public class SecurityManagerTest {
    private static final String PROPERTY_NAME = SecurityManagerTest.class.getName() + ".test";
    private static final String ENV_NAME = "TEST_ENV_VAR";
    private static final String ACCESS_DENIED_DEPLOYMENT = "access-denied.war";
    private static final String ACCESSIBLE_DEPLOYMENT = "accessible.war";

    public static class ConfigureSetupTask extends SystemPropertySetupTask {
        public ConfigureSetupTask() {
            super(Map.of(PROPERTY_NAME, "test.value"));
        }
    }

    @ArquillianResource
    private URI uri;

    @Deployment(name = ACCESS_DENIED_DEPLOYMENT)
    public static WebArchive accessDeniedDeployment() {
        return ShrinkWrap.create(WebArchive.class, ACCESS_DENIED_DEPLOYMENT)
                .addClasses(RestActivator.class, SecurityCheckResource.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Deployment(name = ACCESSIBLE_DEPLOYMENT)
    public static WebArchive accessibleDeployment() {
        return ShrinkWrap.create(WebArchive.class, ACCESSIBLE_DEPLOYMENT)
                .addClasses(RestActivator.class, SecurityCheckResource.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(
                        DeploymentDescriptors.createPermissionsXmlAsset(
                                new PropertyPermission(PROPERTY_NAME, "read"),
                                new ConfigPropertyPermission(PROPERTY_NAME),
                                // Required to fall through to the System.getenv() in the default configuration
                                new ConfigPropertyPermission(ENV_NAME),
                                new ConfigPropertyPermission(Options.ENABLE_DEFAULT_EXCEPTION_MAPPER.name()),
                                // Required to fall through to the default value
                                new RuntimePermission("getenv." + Options.ENABLE_DEFAULT_EXCEPTION_MAPPER.name()),
                                new RuntimePermission("getenv." + ENV_NAME)),
                        "permissions.xml");
    }

    @BeforeAll
    public static void securityManagerOnly() {
        Assumptions.assumeTrue(securityManagerEnabled(),
                "The security manager is not enabled and we are skipping these tests");
    }

    @OperateOnDeployment(ACCESS_DENIED_DEPLOYMENT)
    @Test
    public void envPropertyFailed() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/env/" + ENV_NAME))
                    .request().get();
            final String value = checkFailedResponse(response);
            Assertions.assertTrue(value.contains("\"java.lang.RuntimePermission\" \"getenv." + ENV_NAME + "\""),
                    () -> "Expected the response to have failed with a property permission: " + value);
        }
    }

    @OperateOnDeployment(ACCESS_DENIED_DEPLOYMENT)
    @Test
    public void systemPropertyFailed() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client
                    .target(TestUtil.generateUri(uri, "/test/security/system-property/" + PROPERTY_NAME))
                    .request().get();
            final String value = checkFailedResponse(response);
            Assertions.assertTrue(value.contains("\"java.util.PropertyPermission\" \"" + PROPERTY_NAME + "\""),
                    () -> "Expected the response to have failed with a property permission: " + value);
        }
    }

    @OperateOnDeployment(ACCESS_DENIED_DEPLOYMENT)
    @Test
    public void configPropertyFailed() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/config/" + PROPERTY_NAME))
                    .request().get();
            final String value = checkFailedResponse(response);
            Assertions.assertTrue(
                    value.contains(
                            "\"org.jboss.resteasy.spi.config.security.ConfigPropertyPermission\" \"" + PROPERTY_NAME + "\""),
                    () -> "Expected the response to have failed with a property permission: " + value);
        }
    }

    @OperateOnDeployment(ACCESS_DENIED_DEPLOYMENT)
    @Test
    public void envConfigPropertyFailed() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/config/" + ENV_NAME))
                    .request().get();
            final String value = checkFailedResponse(response);
            Assertions.assertTrue(
                    value.contains("\"org.jboss.resteasy.spi.config.security.ConfigPropertyPermission\" \"" + ENV_NAME + "\""),
                    () -> "Expected the response to have failed with a property permission: " + value);
        }
    }

    @OperateOnDeployment(ACCESS_DENIED_DEPLOYMENT)
    @Test
    public void optionFailed() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/option/"))
                    .request().get();
            final String value = checkFailedResponse(response);
            Assertions.assertTrue(
                    value.contains(
                            "\"org.jboss.resteasy.spi.config.security.ConfigPropertyPermission\" \"dev.resteasy.exception.mapper\""),
                    () -> "Expected the response to have failed with a property permission: " + value);
        }
    }

    @OperateOnDeployment(ACCESSIBLE_DEPLOYMENT)
    @Test
    public void envProperty() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/env/" + ENV_NAME))
                    .request().get();
            final String value = checkSuccessfulResponse(response);
            Assertions.assertEquals(value, "test-env-value");
        }
    }

    @OperateOnDeployment(ACCESSIBLE_DEPLOYMENT)
    @Test
    public void systemProperty() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client
                    .target(TestUtil.generateUri(uri, "/test/security/system-property/" + PROPERTY_NAME))
                    .request().get();
            final String value = checkSuccessfulResponse(response);
            Assertions.assertEquals(value, "test.value");
        }
    }

    @OperateOnDeployment(ACCESSIBLE_DEPLOYMENT)
    @Test
    public void configProperty() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/config/" + PROPERTY_NAME))
                    .request().get();
            final String value = checkSuccessfulResponse(response);
            Assertions.assertEquals(value, "test.value");
        }
    }

    @OperateOnDeployment(ACCESSIBLE_DEPLOYMENT)
    @Test
    public void envConfigProperty() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/config/" + ENV_NAME))
                    .request().get();
            final String value = checkSuccessfulResponse(response);
            Assertions.assertEquals(value, "test-env-value");
        }
    }

    @OperateOnDeployment(ACCESSIBLE_DEPLOYMENT)
    @Test
    public void option() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test/security/option/"))
                    .request().get();
            final String value = checkSuccessfulResponse(response);
            Assertions.assertEquals("true", value, "Expected true, but was false");
        }
    }

    private static String checkFailedResponse(final Response response) {
        final String value = response.readEntity(String.class);
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo(),
                String.format("Expected %s got %s. Response: \"%s\"",
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(), value));
        return value;
    }

    private static String checkSuccessfulResponse(final Response response) {
        final String value = response.readEntity(String.class);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo(),
                String.format("Expected %s got %s. Response: \"%s\"",
                        Response.Status.OK.getStatusCode(), response.getStatus(), value));
        return value;
    }

    private static boolean securityManagerEnabled() {
        final String value = System.getProperty("security.manager");
        return value != null && (value.isBlank() || Boolean.parseBoolean(value));
    }
}
