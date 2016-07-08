package org.jboss.resteasy.test.spring.inmodule.resource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SpringBeanProcessorMyInterceptor implements MethodInterceptor {
    public static boolean invoked = false;

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        invoked = true;
        return methodInvocation.proceed();
    }
}
