package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.logging.Logger;

import jakarta.inject.Inject;

@InterceptorClassMethodInterceptorStereotype
public class InterceptorStereotyped {
    @Inject
    private Logger log;

    public void test() {
        log.info("Stereotyped.test()");
    }
}
