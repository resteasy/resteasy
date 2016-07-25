package org.jboss.resteasy.test.injection.resource;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ProcessSessionBean;
import javax.enterprise.inject.spi.SessionBeanType;

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

    public void addDefinitionError(Throwable t) {
    }

    public Annotated getAnnotated() {
        return null;
    }
}
