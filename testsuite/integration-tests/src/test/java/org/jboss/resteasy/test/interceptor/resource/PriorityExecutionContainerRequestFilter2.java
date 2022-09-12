package org.jboss.resteasy.test.interceptor.resource;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@Priority(0)
public class PriorityExecutionContainerRequestFilter2 implements ContainerRequestFilter {
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {
      PriorityExecutionTest.logger.info(this);
      PriorityExecutionTest.interceptors.add("PriorityExecutionContainerRequestFilter2");
   }
}
