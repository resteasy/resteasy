package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class ConfigurationInheritanceTestFilter2 implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
   }
}
