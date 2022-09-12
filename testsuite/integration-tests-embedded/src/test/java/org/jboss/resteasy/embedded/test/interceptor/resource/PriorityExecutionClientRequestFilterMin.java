package org.jboss.resteasy.embedded.test.interceptor.resource;

import org.jboss.resteasy.embedded.test.interceptor.PriorityExecutionTest;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@Priority(Integer.MIN_VALUE)
public class PriorityExecutionClientRequestFilterMin implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      PriorityExecutionTest.logger.info(this);
      PriorityExecutionTest.interceptors.add("PriorityExecutionClientRequestFilterMin");
   }
}
