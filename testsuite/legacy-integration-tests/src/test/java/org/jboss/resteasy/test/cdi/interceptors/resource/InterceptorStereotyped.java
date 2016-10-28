package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.inject.Inject;
import java.util.logging.Logger;

@InterceptorClassMethodInterceptorStereotype
public class InterceptorStereotyped {
    @Inject
    private Logger log;

    public void test() {
        log.info("Stereotyped.test()");
    }
}
