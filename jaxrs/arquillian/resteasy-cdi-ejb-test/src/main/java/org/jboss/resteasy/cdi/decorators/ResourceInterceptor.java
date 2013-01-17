package org.jboss.resteasy.cdi.decorators;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Interceptor
@ResourceBinding
public class ResourceInterceptor
{
   @Inject private Logger log;
   
   @AroundInvoke
   public Object intercept(InvocationContext ctx) throws Exception
   {
      log.info("entering ResourceInterceptor.intercept()");
      VisitList.add(VisitList.RESOURCE_INTERCEPTOR_ENTER);
      Object result = ctx.proceed();
      VisitList.add(VisitList.RESOURCE_INTERCEPTOR_LEAVE);
      log.info("leaving ResourceInterceptor.intercept()");
      return result;
   }
}

