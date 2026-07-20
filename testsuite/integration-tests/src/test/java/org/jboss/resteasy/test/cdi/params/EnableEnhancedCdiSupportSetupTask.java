/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import java.util.Map;

import org.jboss.resteasy.setup.SystemPropertySetupTask;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class EnableEnhancedCdiSupportSetupTask extends SystemPropertySetupTask {

    public EnableEnhancedCdiSupportSetupTask() {
        super(createSystemProperties());
    }

    private static Map<String, String> createSystemProperties() {
        // The wildfly.system.properties is used to define system properties for WildFly. We don't want to add the
        // dev.resteasy.cdi.enhanced.enabled system property if it's not set as we want to test the default true is
        // being used.
        final String value = System.getProperty("wildfly.system.properties");
        if (value == null || !value.contains("dev.resteasy.cdi.enhanced.enabled")) {
            return Map.of();
        }
        return Map.of("dev.resteasy.cdi.enhanced.enabled", "true");
    }
}
