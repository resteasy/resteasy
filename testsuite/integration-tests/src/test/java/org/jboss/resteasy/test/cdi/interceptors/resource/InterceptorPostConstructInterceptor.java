package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

@InterceptorLifecycleBinding
@Interceptor
public class InterceptorPostConstructInterceptor {
    @Inject
    private Logger log;

    @PostConstruct
    public void intercept(InvocationContext ctx) throws Exception {
        log.info("*** Intercepting call in InterceptorPostConstructInterceptor.intercept()");
        InterceptorVisitList.add(this);
        ctx.proceed();
        log.info("*** Back from intercepting call in InterceptorPostConstructInterceptor.intercept()");
    }
}
