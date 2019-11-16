package org.jboss.resteasy.test.cdi.ejb.resource;

import javax.validation.constraints.Size;

public class EJBCDIValidationEJBProxyGreeting {

    @Size(min = 0, max = 10, message = "value should be between 0 and 10")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
