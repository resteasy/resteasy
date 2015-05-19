package org.jboss.resteasy.spi.interception;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.WebApplicationException;

/**
 * Executed before resource method (not resource locator methods though!)
 * <p/>
 * The interceptor can decide to return its own response.  This will result in not invoking the resource method.
 *
 * @deprecated
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public interface PreProcessInterceptor
{
   /**
    * Preprocess resource method invocation
    *
    * @param request
    * @return null unless the interceptor is returning its own response
    */
   ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker method) throws Failure, WebApplicationException;
}
