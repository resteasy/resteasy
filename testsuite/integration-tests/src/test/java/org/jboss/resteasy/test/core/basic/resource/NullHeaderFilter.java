package org.jboss.resteasy.test.core.basic.resource;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class NullHeaderFilter implements ClientRequestFilter {
   
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      MultivaluedMap<String, Object> headers = requestContext.getHeaders();
      headers.add("X-Client-Header", null);
      System.out.println("added X-Client-Header");
   }
}
