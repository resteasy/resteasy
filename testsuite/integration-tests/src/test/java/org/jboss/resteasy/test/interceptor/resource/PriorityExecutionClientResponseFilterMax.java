package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

@Priority(Integer.MAX_VALUE)
public class PriorityExecutionClientResponseFilterMax implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionClientResponseFilterMax");
    }
}
