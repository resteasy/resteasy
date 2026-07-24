/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.extractors;

import org.jboss.resteasy.spi.HttpRequest;

/**
 * Extracts a parameter value from an {@link HttpRequest}. Each implementation is bound to a specific parameter
 * annotation (e.g. {@code @PathParam}, {@code @QueryParam}) and knows how to locate and convert the raw request
 * value into the target Java type.
 * <p>
 * Instances are typically created by {@link ParameterExtractors} and invoked once per request.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@FunctionalInterface
public interface RequestParameterExtractor {

    /**
     * Extracts and converts a parameter value from the given request.
     *
     * @param request the current HTTP request
     *
     * @return the extracted value, or {@code null} if the parameter is absent and no default is configured
     */
    Object extract(HttpRequest request);
}
