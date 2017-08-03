package org.jboss.resteasy.test.client.proxy.resource;

public class NullEntityProxyResource implements NullEntityProxy {

    public NullEntityProxyGreeting helloEntity(NullEntityProxyGreeter greeter) {
        return new NullEntityProxyGreeting(greeter);
    }
}

