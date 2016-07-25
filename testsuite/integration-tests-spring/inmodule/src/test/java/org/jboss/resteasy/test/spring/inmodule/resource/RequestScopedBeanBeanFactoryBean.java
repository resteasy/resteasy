package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created with IntelliJ IDEA.
 * User: sgv
 * Date: 23/10/14
 * Time: 16:15
 */
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
