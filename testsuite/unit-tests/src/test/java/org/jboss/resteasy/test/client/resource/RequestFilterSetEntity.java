package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;

public class RequestFilterSetEntity implements ClientRequestFilter {

   private static Logger logger = Logger.getLogger(RequestFilterSetEntity.class);

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      logger.info("*** filter 1 ***");
      requestContext.setEntity("test", null, MediaType.APPLICATION_JSON_TYPE);
   }
}
