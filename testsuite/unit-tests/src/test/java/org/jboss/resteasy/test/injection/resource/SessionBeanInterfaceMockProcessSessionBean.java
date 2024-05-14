package org.jboss.resteasy.test.injection.resource;

import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.ProcessSessionBean;
import jakarta.enterprise.inject.spi.SessionBeanType;
import jakarta.enterprise.invoke.Invoker;
import jakarta.enterprise.invoke.InvokerBuilder;

public class SessionBeanInterfaceMockProcessSessionBean<T> implements ProcessSessionBean<T> {

    private Bean<Object> bean;

    public SessionBeanInterfaceMockProcessSessionBean(final Bean<Object> bean) {
        this.bean = bean;
    }

    public Bean<Object> getBean() {
        return bean;
    }

    public String getEjbName() {
        return null;
    }

    public SessionBeanType getSessionBeanType() {
        return null;
    }

    public AnnotatedType<Object> getAnnotatedBeanClass() {
        return null;
    }

    @Override
    public InvokerBuilder<Invoker<Object, ?>> createInvoker(final AnnotatedMethod<? super Object> method) {
        return null;
    }

    public void addDefinitionError(Throwable t) {
    }

    public Annotated getAnnotated() {
        return null;
    }
}
