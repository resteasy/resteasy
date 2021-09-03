package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.util.HttpHeaderNames;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;


/**
 * Created by rsearls on 8/24/17.
 */
@Provider
public class ProxyNullInputStreamClientResponseFilter implements ClientResponseFilter {
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
   {
      MultivaluedMap<String,String> headers = responseContext.getHeaders();
      // required header to mimic and force required failure behavior
      headers.add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
   }
}
