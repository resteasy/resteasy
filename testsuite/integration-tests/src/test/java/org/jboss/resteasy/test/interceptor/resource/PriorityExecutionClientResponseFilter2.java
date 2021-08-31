package org.jboss.resteasy.test.interceptor.resource;

import org.jboss.resteasy.test.interceptor.PriorityExecutionTest;

import javax.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

@Priority(0)
public class PriorityExecutionClientResponseFilter2 implements ClientResponseFilter {
   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      PriorityExecutionTest.logger.info(this);
      PriorityExecutionTest.interceptors.add("PriorityExecutionClientResponseFilter2");
   }
}
