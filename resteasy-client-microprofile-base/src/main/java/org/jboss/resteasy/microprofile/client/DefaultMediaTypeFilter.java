package org.jboss.resteasy.microprofile.client;

import javax.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.MediaType;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;

@Priority(Integer.MIN_VALUE)
public class DefaultMediaTypeFilter implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        String header = responseContext.getHeaderString(CONTENT_TYPE);
        if (header == null) {
            responseContext.getHeaders().putSingle(CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
        }
    }
}
