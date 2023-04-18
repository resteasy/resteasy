package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.test.client.proxy.ContextTest;
import org.junit.Assert;

public class ContextTestResource implements ContextTest.ResourceInterface {

    public String echo(UriInfo info) {
        Assert.assertNotNull("UriInfo was not injected into methods call", info);
        return "content";
    }
}
