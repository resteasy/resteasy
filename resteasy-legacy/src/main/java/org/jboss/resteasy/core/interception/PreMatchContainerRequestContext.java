package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext instead.
 */
@Deprecated
public class PreMatchContainerRequestContext extends org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext
{

   public PreMatchContainerRequestContext(HttpRequest request)
   {
      super(request);
   }
}
