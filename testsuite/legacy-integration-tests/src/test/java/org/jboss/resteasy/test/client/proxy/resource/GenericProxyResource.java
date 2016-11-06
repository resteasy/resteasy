package org.jboss.resteasy.test.client.proxy.resource;

public class GenericProxyResource implements GenericProxySpecificProxy {
    @Override
    public String sayHi(String in) {
        return in;
    }
}
