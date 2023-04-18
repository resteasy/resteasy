package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.util.HttpHeaderNames;

/**
 * Created by rsearls on 8/24/17.
 */
@Provider
public class ProxyNullInputStreamClientResponseFilter implements ClientResponseFilter {
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        MultivaluedMap<String, String> headers = responseContext.getHeaders();
        // required header to mimic and force required failure behavior
        headers.add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
    }
}
