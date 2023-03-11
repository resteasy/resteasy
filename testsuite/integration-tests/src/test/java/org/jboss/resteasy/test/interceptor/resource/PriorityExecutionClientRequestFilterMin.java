package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

@Priority(Integer.MIN_VALUE)
public class PriorityExecutionClientRequestFilterMin implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionClientRequestFilterMin");
    }
}
