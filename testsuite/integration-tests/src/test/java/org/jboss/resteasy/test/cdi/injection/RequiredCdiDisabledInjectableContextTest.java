/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.SystemPropertySetupTask;
import org.jboss.resteasy.test.cdi.injection.resource.RequiredInjectableContextResource;
import org.jboss.shrinkwrap.api.Archive;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
@ServerSetup(RequiredCdiDisabledInjectableContextTest.DisableEnhancedCdiSupportSetupTask.class)
public class RequiredCdiDisabledInjectableContextTest extends AbstractRequiredInjectableContextTest {

    public RequiredCdiDisabledInjectableContextTest() {
        super("/context");
    }

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return defaultDeployment(RequiredCdiDisabledInjectableContextTest.class)
                .addClasses(RequiredInjectableContextResource.class);
    }

    public static class DisableEnhancedCdiSupportSetupTask extends SystemPropertySetupTask {

        public DisableEnhancedCdiSupportSetupTask() {
            super(Map.of("dev.resteasy.cdi.enhanced.enabled", "false"));
        }
    }
}
