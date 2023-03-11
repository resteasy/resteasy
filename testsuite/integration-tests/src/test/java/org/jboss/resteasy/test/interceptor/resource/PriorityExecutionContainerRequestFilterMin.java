package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

@Priority(Integer.MIN_VALUE)
public class PriorityExecutionContainerRequestFilterMin implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionContainerRequestFilterMin");
    }
}
