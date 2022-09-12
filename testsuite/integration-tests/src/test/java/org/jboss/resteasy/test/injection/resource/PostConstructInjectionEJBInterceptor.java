package org.jboss.resteasy.test.injection.resource;

import jakarta.annotation.PostConstruct;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
public class PostConstructInjectionEJBInterceptor {
    @PostConstruct
    public Object methodInterceptor(InvocationContext ctx) throws Exception {
        Object o = ctx.getTarget();
        if (o instanceof PostConstructInjectionEJBResource) {
            PostConstructInjectionEJBResource resource = (PostConstructInjectionEJBResource) o;
            resource.setT("wxyz");
        }
        return ctx.proceed();
    }
}
