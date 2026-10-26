/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.RequestListener;

/**
 * Destroys the {@code @Dependent} instances RESTEasy created through CDI during a request once the request has
 * completed.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public final class CdiRequestListener implements RequestListener {

    @Override
    public void requestCompleted(final HttpRequest request, final HttpResponse response) {
        BeanManagerSupport.releaseReferences(request);
    }
}
