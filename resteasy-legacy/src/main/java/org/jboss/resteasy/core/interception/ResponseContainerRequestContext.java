package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ResponseContainerRequestContext instead
 */
@Deprecated
public class ResponseContainerRequestContext extends org.jboss.resteasy.core.interception.jaxrs.ResponseContainerRequestContext
{

   public ResponseContainerRequestContext(HttpRequest request)
   {
      super(request);
   }
}
