package org.jboss.resteasy.resteasy1008;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 21, 2012
 */
public class TestInterceptor
{
   private static final Logger log = LoggerFactory.getLogger(TestInterceptor.class);
   
   public TestInterceptor()
   {
      System.out.println("creating Interceptor0");
   }
   
   @AroundInvoke
   public Object intercept(InvocationContext ctx) throws Exception
   {
      log.info("*** Intercepting call in Interceptor0.intercept()");
      Object[] params = ctx.getParameters();
      params[0] = 0;
      log.info("setting parameter to " + params[0]);
      ctx.setParameters(params);
      Object result = ctx.proceed();
      log.info("*** Back from intercepting call in Interceptor0.intercept()");
      return result;
   }
}

