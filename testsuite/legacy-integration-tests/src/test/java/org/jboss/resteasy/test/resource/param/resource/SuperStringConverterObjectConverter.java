package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.spi.StringConverter;

public abstract class SuperStringConverterObjectConverter<T> implements StringConverter<T> {
    public String toString(T value) {
        return value.toString();
    }
}
