package org.jboss.resteasy.test.client.resource;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.junit.Assert;

public class ClientResponseFilterAllowed implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        Set<String> allowed = responseContext.getAllowedMethods();
        Assert.assertTrue(allowed.contains("OPTIONS"));
    }
}
