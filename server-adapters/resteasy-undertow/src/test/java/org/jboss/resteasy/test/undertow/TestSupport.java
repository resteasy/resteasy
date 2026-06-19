/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.undertow;

import org.jboss.resteasy.util.PortProvider;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class TestSupport {

    /**
     * Generate a URL incorporating the configured port.
     *
     * @param path the path
     * @return a full URL
     */
    public static String generateURL(String path) {
        return String.format("http://%s:%d%s", PortProvider.getHost(), PortProvider.getPort(), path);
    }
}
