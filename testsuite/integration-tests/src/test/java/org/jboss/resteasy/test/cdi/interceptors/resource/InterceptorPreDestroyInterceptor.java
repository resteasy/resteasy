package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

@InterceptorLifecycleBinding
@Interceptor
public class InterceptorPreDestroyInterceptor {
    @Inject
    private Logger log;

    @PreDestroy
    public void intercept(InvocationContext ctx) throws Exception {
        log.info("*** Intercepting call in InterceptorPreDestroyInterceptor.intercept()");
        InterceptorVisitList.add(this);
        ctx.proceed();
        log.info("*** Back from intercepting call in InterceptorPreDestroyInterceptor.intercept()");
    }
}
