package org.jboss.resteasy.test.resource.resource;

public class SimpleFromValueProvider {

    public String fromValue(String s) {
        throw new RuntimeException("Force error");
    }
}
