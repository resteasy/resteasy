/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.interceptor.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.resteasy.embedded.test.interceptor.PriorityExecutionTest;

@Priority(100)
public class PriorityExecutionClientRequestFilter3 implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionClientRequestFilter3");
    }
}
