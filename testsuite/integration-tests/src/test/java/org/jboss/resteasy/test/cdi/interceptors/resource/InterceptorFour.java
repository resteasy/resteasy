package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

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

