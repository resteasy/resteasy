package org.jboss.resteasy.test.cdi.interceptors.resource;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.logging.Logger;

@Interceptor
@InterceptorWriterBinding
public class InterceptorBookWriterInterceptorInterceptor {
   @Inject
   private Logger log;

   @AroundInvoke
   public Object intercept(InvocationContext ctx) throws Exception {
      log.info("*** Intercepting call in InterceptorBookWriterInterceptorInterceptor.intercept()");
      InterceptorVisitList.add(this);
      Object result = ctx.proceed();
      log.info("*** Back from intercepting call in InterceptorBookWriterInterceptorInterceptor.intercept()");
      return result;
   }
}
