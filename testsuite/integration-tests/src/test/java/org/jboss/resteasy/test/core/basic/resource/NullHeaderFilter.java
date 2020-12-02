package org.jboss.resteasy.test.core.basic.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NullHeaderFilter implements ClientRequestFilter {

   private static final Logger LOG = LogManager.getLogger(NullHeaderFilter.class);

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      MultivaluedMap<String, Object> headers = requestContext.getHeaders();
      headers.add("X-Client-Header", null);
      LOG.info("added X-Client-Header");
   }
}
