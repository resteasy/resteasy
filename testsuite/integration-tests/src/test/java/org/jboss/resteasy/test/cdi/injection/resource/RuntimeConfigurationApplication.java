/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ApplicationPath("/")
public class RuntimeConfigurationApplication extends Application {
}
