package org.jboss.resteasy.test.cdi.basic.resource;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

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

