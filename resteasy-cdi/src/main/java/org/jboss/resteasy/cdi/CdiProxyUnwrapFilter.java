/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

/**
 * A {@link ContainerResponseFilter} that unwraps CDI proxies from response entities before serialization. Normal-scoped
 * CDI beans (e.g. {@link jakarta.enterprise.context.RequestScoped @RequestScoped}) are returned as proxy instances whose
 * fields contain default values. Serialization frameworks such as Jackson that use field access or introspect the proxy
 * class directly may serialize proxy-internal properties (e.g. Weld's {@code getMetadata()}) or produce incorrect
 * values. This filter replaces the proxy with the underlying contextual instance so that serialization sees the real
 * object.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @see CdiProxyUnwrapper
 */
@Priority(Priorities.HEADER_DECORATOR)
public class CdiProxyUnwrapFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        final Object entity = responseContext.getEntity();
        if (entity != null) {
            final Object unwrapped = CdiProxyUnwrapper.unwrapIfRequired(entity);
            if (unwrapped != entity) {
                responseContext.setEntity(unwrapped);
            }
        }
    }
}
