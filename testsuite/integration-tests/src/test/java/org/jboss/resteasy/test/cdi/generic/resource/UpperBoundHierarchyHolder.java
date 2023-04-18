package org.jboss.resteasy.test.cdi.generic.resource;

import java.lang.reflect.Type;

public class UpperBoundHierarchyHolder<T extends HierarchyHolder<? extends Primate>> {
    private Class<?> clazz;

    public UpperBoundHierarchyHolder(final Class<?> clazz) {
        this.clazz = clazz;
    }

    Type getTypeArgument() {
        return clazz;
    }
}
