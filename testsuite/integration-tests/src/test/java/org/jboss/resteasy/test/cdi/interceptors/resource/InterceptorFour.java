package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@InterceptorMethodBinding
public class InterceptorFour {
    @Inject
    private Logger log;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        log.info("*** Intercepting call in InterceptorFour.intercept()");
        InterceptorVisitList.add(this);
        Object result = ctx.proceed();
        log.info("*** Back from intercepting call in InterceptorFour.intercept()");
        return result;
    }
}
