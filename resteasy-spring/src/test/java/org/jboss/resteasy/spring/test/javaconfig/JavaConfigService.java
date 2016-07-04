package org.jboss.resteasy.spring.test.javaconfig;

/**
 * This POJO service bean will get injected into the resource.
 */
public class JavaConfigService {
    public String invoke() {
        return "hello";
    }
}
