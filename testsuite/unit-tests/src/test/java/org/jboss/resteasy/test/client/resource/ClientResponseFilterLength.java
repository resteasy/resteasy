package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.junit.Assert;

public class ClientResponseFilterLength implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        Assert.assertEquals("The length of the response is not the expected one", 10, responseContext.getLength());
    }
}
