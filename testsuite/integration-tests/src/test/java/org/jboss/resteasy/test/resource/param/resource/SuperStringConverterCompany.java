package org.jboss.resteasy.test.resource.param.resource;

public class SuperStringConverterCompany {
    private final String name;

    public SuperStringConverterCompany(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
