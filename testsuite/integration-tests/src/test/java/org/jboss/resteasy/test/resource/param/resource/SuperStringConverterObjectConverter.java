package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public abstract class SuperStringConverterObjectConverter<T> implements ParamConverter<T> {
    public String toString(T value) {
        return value.toString();
    }
}
