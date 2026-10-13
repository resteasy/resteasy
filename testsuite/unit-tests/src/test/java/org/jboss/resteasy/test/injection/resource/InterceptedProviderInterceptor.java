package org.jboss.resteasy.test.injection.resource;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@InterceptedProviderBinding
@Priority(Interceptor.Priority.APPLICATION)
public class InterceptedProviderInterceptor {

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        return context.proceed();
    }
}
