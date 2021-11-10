package org.jboss.resteasy.test.providers.custom.resource;


import org.jboss.logging.Logger;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class DuplicateProviderRegistrationFilter implements ClientRequestFilter {
   private static Logger logger = Logger.getLogger(DuplicateProviderRegistrationFilter.class);

   @Override
   public void filter(ClientRequestContext clientRequestContext) throws IOException {
      logger.info(DuplicateProviderRegistrationFilter.class.getName());
   }
}
