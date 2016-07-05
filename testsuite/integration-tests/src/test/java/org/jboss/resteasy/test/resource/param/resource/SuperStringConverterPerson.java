package org.jboss.resteasy.test.resource.param.resource;

public class SuperStringConverterPerson {
    private final String name;

    public SuperStringConverterPerson(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
