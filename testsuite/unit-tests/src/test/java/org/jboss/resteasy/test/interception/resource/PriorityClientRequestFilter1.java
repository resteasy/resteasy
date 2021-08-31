package org.jboss.resteasy.test.interception.resource;

import javax.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@Priority(100)
public class PriorityClientRequestFilter1 implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {

   }
}
