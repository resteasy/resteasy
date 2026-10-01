/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.core.config;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.test.core.config.resource.ConfigurationFactoryResource;
import org.jboss.resteasy.test.core.config.resource.HigherPriorityConfigurationFactory;
import org.jboss.resteasy.test.core.config.resource.LowerPriorityConfigurationFactory;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Regression test verifying that the default {@link ConfigurationFactory#getInstance()} behavior
 * is unchanged in WildFly. With the resolver property disabled (default), WildFly's own
 * {@code WildFlyConfigurationFactory} should be returned for all deployments.
 * <p>
 * Each deployment includes its own {@link ConfigurationFactory} implementation, but since the
 * resolver is not enabled, these are not discovered — WildFly's deployment processor provides
 * the factory via its own mechanism.
 * </p>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ConfigurationFactoryIsolationTest {

    private static final String WILDFLY_CONFIGURATION_FACTORY = "org.jboss.as.jaxrs.deployment.WildFlyConfigurationFactory";
    private static final String DEPLOYMENT_A = "deploymentA";
    private static final String DEPLOYMENT_B = "deploymentB";

    @Deployment(name = DEPLOYMENT_A)
    public static Archive<?> deployA() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_A);
        war.addClass(HigherPriorityConfigurationFactory.class);
        war.addAsServiceProvider(ConfigurationFactory.class, HigherPriorityConfigurationFactory.class);
        return TestUtil.finishContainerPrepare(war, null, ConfigurationFactoryResource.class);
    }

    @Deployment(name = DEPLOYMENT_B)
    public static Archive<?> deployB() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_B);
        war.addClass(LowerPriorityConfigurationFactory.class);
        war.addAsServiceProvider(ConfigurationFactory.class, LowerPriorityConfigurationFactory.class);
        return TestUtil.finishContainerPrepare(war, null, ConfigurationFactoryResource.class);
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT_A)
    public void testDefaultBehaviorUsesWildFlyFactoryForDeploymentA() {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(PortProviderUtil.generateURL("/config/factory-class", DEPLOYMENT_A))
                    .request()
                    .get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(WILDFLY_CONFIGURATION_FACTORY, response.readEntity(String.class),
                    "Default behavior should use WildFlyConfigurationFactory");
        }
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT_B)
    public void testDefaultBehaviorUsesWildFlyFactoryForDeploymentB() {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(PortProviderUtil.generateURL("/config/factory-class", DEPLOYMENT_B))
                    .request()
                    .get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(WILDFLY_CONFIGURATION_FACTORY, response.readEntity(String.class),
                    "Default behavior should use WildFlyConfigurationFactory");
        }
    }
}
