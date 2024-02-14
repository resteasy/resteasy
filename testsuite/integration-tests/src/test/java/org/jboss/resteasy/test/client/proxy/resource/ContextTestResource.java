package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.test.client.proxy.ContextTest;
import org.junit.jupiter.api.Assertions;

public class ContextTestResource implements ContextTest.ResourceInterface {

    public String echo(UriInfo info) {
        Assertions.assertNotNull(info, "UriInfo was not injected into methods call");
        return "content";
    }
}
