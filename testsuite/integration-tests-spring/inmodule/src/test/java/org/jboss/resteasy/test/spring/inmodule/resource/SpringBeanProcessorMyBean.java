package org.jboss.resteasy.test.spring.inmodule.resource;

public class SpringBeanProcessorMyBean {

    public SpringBeanProcessorMyInnerBean getMyInnerBean() {
        return new SpringBeanProcessorSpringBeanProcessorMyInnerBeanImpl();
    }
}
