package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.resteasy.spi.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.ext.Provider;

@Provider
public class SpringBeanProcessorCustomerStringConverter implements StringConverter<SpringBeanProcessorCustomer> {

    // this isn't a complex service, but it provides a test to confirm that
    // RESTEasy doesn't muck up the @Autowired annotation handling in the Spring
    // life-cycle
    @Autowired
    SpringBeanProcessorCustomerService service;

    @Override
    public SpringBeanProcessorCustomer fromString(String name) {
        return service.convert(name);
    }

    public String toString(SpringBeanProcessorCustomer springBeanProcessorCustomer) {
        return service.convert(springBeanProcessorCustomer);
    }

}
