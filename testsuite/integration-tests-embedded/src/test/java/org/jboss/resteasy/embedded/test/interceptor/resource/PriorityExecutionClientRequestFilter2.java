package org.jboss.resteasy.embedded.test.interceptor.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.resteasy.embedded.test.interceptor.PriorityExecutionTest;

@Priority(0)
public class PriorityExecutionClientRequestFilter2 implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionClientRequestFilter2");
    }
}
