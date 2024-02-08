package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BeanReaderWriterConfigBean {
    public String version() {
        return "1.1";
    }
}
