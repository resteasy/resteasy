/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import java.util.Map;

import jakarta.json.JsonValue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.SystemPropertySetupTask;
import org.jboss.resteasy.test.cdi.params.resources.DisabledEnhancedCdiResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that when the {@code dev.resteasy.cdi.enhanced.enabled} configuration property is set to {@code false},
 * standard Jakarta REST parameter injection still works and {@code @*Param} annotations are not registered as CDI
 * qualifiers.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
@ServerSetup(DisabledEnhancedCdiTest.DisableEnhancedCdiSupportSetupTask.class)
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.3")
@Tag("requires-enhanced-cdi")
class DisabledEnhancedCdiTest extends AbstractParamTest {

    DisabledEnhancedCdiTest() {
        super("disabled", JsonValue.FALSE);
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return defaultDeployment(DisabledEnhancedCdiTest.class).addClass(DisabledEnhancedCdiResource.class);
    }

    @Override
    @Disabled("Setter method parameters annotated with @*Param do not work without CDI")
    void methodParametersAnnotated() {
        super.methodParametersAnnotated();
    }

    public static class DisableEnhancedCdiSupportSetupTask extends SystemPropertySetupTask {

        public DisableEnhancedCdiSupportSetupTask() {
            super(Map.of("dev.resteasy.cdi.enhanced.enabled", "false"));
        }
    }

}
