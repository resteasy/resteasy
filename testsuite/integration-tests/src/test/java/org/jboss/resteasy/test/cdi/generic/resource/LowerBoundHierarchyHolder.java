package org.jboss.resteasy.test.cdi.generic.resource;

import java.lang.reflect.Type;

public class LowerBoundHierarchyHolder<T extends HierarchyHolder<? super Primate>> {
    private Class<?> clazz;

    public LowerBoundHierarchyHolder(final Class<?> clazz) {
        this.clazz = clazz;
    }

    Type getTypeArgument() {
        return clazz;
    }
}
