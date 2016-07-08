package org.jboss.resteasy.test.spring.deployment.resource;

import org.springframework.beans.factory.FactoryBean;

public class RequestScopedBeanBeanFactoryBean implements FactoryBean<RequestScopedBeanBean> {

    @Override
    public RequestScopedBeanBean getObject() throws Exception {
        return new RequestScopedBeanBean();
    }

    @Override
    public Class<?> getObjectType() {
        return RequestScopedBeanBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
