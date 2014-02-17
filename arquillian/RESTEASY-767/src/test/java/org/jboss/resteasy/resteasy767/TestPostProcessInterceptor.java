package org.jboss.resteasy.resteasy767;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2013
 */
@Provider
@ServerInterceptor
public class TestPostProcessInterceptor implements PostProcessInterceptor
{
   public static boolean called;

   @Override
   public void postProcess(ServerResponse response)
   {
      called = true;
   }
}
