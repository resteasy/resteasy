package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.spi.StringConverter;

public abstract class SuperStringConverterSuperPersonConverter implements StringConverter<SuperStringConverterPerson> {
    public SuperStringConverterPerson fromString(String value) {
        return new SuperStringConverterPerson(value);
    }

    public String toString(SuperStringConverterPerson value) {
        return value.getName();
    }
}
