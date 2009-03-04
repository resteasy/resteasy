package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.WebApplicationException;

/**
 * Executed before resource method (not resource locator methods though!)
 * <p/>
 * The interceptor can decide to return its own response.  This will result in not invoking the resource method.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface PreProcessInterceptor
{
   /**
    * Preprocess resource method invocation
    *
    * @param request
    * @return null unless the interceptor is returning its own response
    */
   ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException;
}
