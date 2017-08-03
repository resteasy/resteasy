package org.jboss.resteasy.test.client.proxy.resource;

public class NullEntityProxyGreeting {
    NullEntityProxyGreeter greeter;

    public NullEntityProxyGreeting(NullEntityProxyGreeter greeter) {
        this.greeter = greeter;
    }

    public NullEntityProxyGreeting() {
    }

    public NullEntityProxyGreeter getGreeter() {
        return greeter;
    }

    public void setGreeter(NullEntityProxyGreeter greeter) {
        this.greeter = greeter;
    }
}
