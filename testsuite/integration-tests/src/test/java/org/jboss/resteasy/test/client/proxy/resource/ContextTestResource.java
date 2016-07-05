package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.test.client.proxy.ContextTest;
import org.junit.Assert;

import javax.ws.rs.core.UriInfo;

public class ContextTestResource implements ContextTest.ResourceInterface {

    public String echo(UriInfo info) {
        Assert.assertNotNull("UriInfo was not injected into methods call", info);
        return "content";
    }
}
