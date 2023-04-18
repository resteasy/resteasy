package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.junit.Assert;

public class ClientResponseFilterNullHeaderString implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        String header = responseContext.getHeaderString("header1");
        Assert.assertNotNull("The header is empty", header);
        Assert.assertTrue("The header value doesn't match the expected one", header.equals(""));
    }
}
