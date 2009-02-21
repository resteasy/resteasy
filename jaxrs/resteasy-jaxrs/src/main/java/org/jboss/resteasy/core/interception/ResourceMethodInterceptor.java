package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;

import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceMethodInterceptor
{
   /**
    * Whether or not this ResourceMethod should be intercepted.  This is called at deployment time when setting
    * up interceptor chains.
    *
    * @param method
    * @return
    */
   boolean accepted(ResourceMethod method);

   public ServerResponse invoke(ResourceMethodContext ctx) throws Failure, ApplicationException, WebApplicationException;
}
