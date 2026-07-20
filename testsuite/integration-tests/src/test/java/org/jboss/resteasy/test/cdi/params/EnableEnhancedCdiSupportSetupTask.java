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
        super(Map.of("dev.resteasy.cdi.enhanced.enabled", "true"));
    }
}
