package org.jboss.resteasy.spring.beanprocessor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MyInterceptor implements MethodInterceptor
{
   public static boolean invoked = false;

   public Object invoke(MethodInvocation methodInvocation) throws Throwable
   {
      invoked = true;
      return methodInvocation.proceed();
   }
}
