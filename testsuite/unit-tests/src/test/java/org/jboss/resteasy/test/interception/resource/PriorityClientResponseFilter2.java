package org.jboss.resteasy.test.interception.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

@Priority(200)
public class PriorityClientResponseFilter2 implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

    }
}
