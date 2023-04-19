package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@InterceptorReaderBinding
public class InterceptorBookReaderInterceptorInterceptor {
    @Inject
    private Logger log;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        log.info("*** Intercepting call in InterceptorBookReaderInterceptorInterceptor.intercept()");
        InterceptorVisitList.add(this);
        Object result = ctx.proceed();
        log.info("*** Back from intercepting call in InterceptorBookReaderInterceptorInterceptor.intercept()");
        return result;
    }
}
