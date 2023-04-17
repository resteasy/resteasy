package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

@Priority(-100)
public class PriorityExecutionContainerResponseFilter1 implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionContainerResponseFilter1");
    }
}
