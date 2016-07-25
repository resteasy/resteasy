package org.jboss.resteasy.test.interceptor.resource;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@Priority(100)
public class PriorityExecutionContainerRequestFilter3 implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionContainerRequestFilter3");
    }
}
