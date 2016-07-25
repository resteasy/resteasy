package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

public class InterceptorTwo {
    @Inject
    private Logger log;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        log.info("*** Intercepting call in InterceptorTwo.intercept()");
        InterceptorVisitList.add(this);
        Object result = ctx.proceed();
        log.info("*** Back from intercepting call in InterceptorTwo.intercept()");
        return result;
    }
}

