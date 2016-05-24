package org.jboss.resteasy.cdi.interceptors;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 19, 2012
 */
@LifecycleBinding
@Interceptor
public class PostConstructInterceptor
{
   @Inject private Logger log;
   
   @PostConstruct
   public void intercept(InvocationContext ctx) throws Exception
   {
      log.info("*** Intercepting call in PostConstructInterceptor.intercept()");
      VisitList.add(this);
      ctx.proceed();
      log.info("*** Back from intercepting call in PostConstructInterceptor.intercept()");
   }
}
