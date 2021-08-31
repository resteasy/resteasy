package org.jboss.resteasy.test.interceptor.resource;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

import javax.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@Priority(Integer.MAX_VALUE)
public class PriorityExecutionClientRequestFilterMax implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      PriorityExecutionTest.logger.info(this);
      PriorityExecutionTest.interceptors.add("PriorityExecutionClientRequestFilterMax");
   }
}
