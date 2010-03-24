package org.jboss.resteasy.cdi.test.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@TestInterceptorBinding
public class TestInterceptor
{
   @AroundInvoke
   public Object intercept(InvocationContext ctx) throws Exception
   {
      // Do a negation
      return !((Boolean) ctx.proceed());
   }
}
