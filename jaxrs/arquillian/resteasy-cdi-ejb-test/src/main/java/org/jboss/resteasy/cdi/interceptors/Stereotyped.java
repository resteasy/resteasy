package org.jboss.resteasy.cdi.interceptors;

import java.util.logging.Logger;

import javax.inject.Inject;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 4, 2012
 */
@ClassMethodInterceptorStereotype
public class Stereotyped
{
   @Inject private Logger log;
   
   public void test()
   {
      log.info("Stereotyped.test()");
   }
}
