package org.jboss.resteasy.test.cdi.generic.resource;

import java.lang.reflect.Type;

public class NestedHierarchyHolder<T> {
    private Class<?> clazz;

    public NestedHierarchyHolder(final Class<?> clazz) {
        this.clazz = clazz;
    }

    Type getTypeArgument() {
        return clazz;
    }
}
