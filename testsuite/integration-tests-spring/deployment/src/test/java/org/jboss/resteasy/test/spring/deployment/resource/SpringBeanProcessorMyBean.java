package org.jboss.resteasy.test.spring.deployment.resource;

public class SpringBeanProcessorMyBean {

    public SpringBeanProcessorMyInnerBean getMyInnerBean() {
        return new SpringBeanProcessorSpringBeanProcessorMyInnerBeanImpl();
    }
}
