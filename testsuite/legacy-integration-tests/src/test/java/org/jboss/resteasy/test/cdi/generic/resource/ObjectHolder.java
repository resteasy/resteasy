package org.jboss.resteasy.test.cdi.generic.resource;

import java.lang.reflect.Type;

public class ObjectHolder<T> {
    private Class<T> clazz;

    public ObjectHolder(final Class<T> clazz) {
        this.clazz = clazz;
    }

    Type getTypeArgument() {
        return clazz;
    }
}
