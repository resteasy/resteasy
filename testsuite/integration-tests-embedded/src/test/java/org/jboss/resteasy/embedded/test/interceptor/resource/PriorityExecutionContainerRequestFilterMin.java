package org.jboss.resteasy.embedded.test.interceptor.resource;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.embedded.test.interceptor.PriorityExecutionTest;

@Priority(Integer.MIN_VALUE)
public class PriorityExecutionContainerRequestFilterMin implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        PriorityExecutionTest.logger.info(this);
        PriorityExecutionTest.interceptors.add("PriorityExecutionContainerRequestFilterMin");
    }
}
