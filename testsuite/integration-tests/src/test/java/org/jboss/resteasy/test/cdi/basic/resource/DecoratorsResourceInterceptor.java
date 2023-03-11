package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@DecoratorsResourceBinding
public class DecoratorsResourceInterceptor {
    @Inject
    private Logger log;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        log.info("entering DecoratorsResourceInterceptor.intercept()");
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_INTERCEPTOR_ENTER);
        Object result = ctx.proceed();
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_INTERCEPTOR_LEAVE);
        log.info("leaving DecoratorsResourceInterceptor.intercept()");
        return result;
    }
}
