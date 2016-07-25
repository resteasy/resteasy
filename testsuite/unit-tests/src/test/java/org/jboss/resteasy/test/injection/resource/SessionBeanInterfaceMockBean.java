package org.jboss.resteasy.test.injection.resource;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public class SessionBeanInterfaceMockBean<T> implements Bean<T> {
    private Class<?> beanClass;
    private Set<Type> types;

    public SessionBeanInterfaceMockBean(final Class<?> beanClass, final Set<Type> types) {
        this.beanClass = beanClass;
        this.types = types;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Set<Type> getTypes() {
        return types;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return null;
    }

    public String getName() {
        return null;
    }

    public Set<Annotation> getQualifiers() {
        return null;
    }

    public Class<? extends Annotation> getScope() {
        return null;
    }

    public Set<Class<? extends Annotation>> getStereotypes() {
        return null;
    }

    public boolean isAlternative() {
        return false;
    }

    public boolean isNullable() {
        return false;
    }

    public T create(CreationalContext<T> creationalContext) {
        return null;
    }

    public void destroy(T instance, CreationalContext<T> creationalContext) {
    }
}
