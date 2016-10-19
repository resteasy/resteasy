package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext instead.
 */
@Deprecated
public class PostMatchContainerRequestContext extends org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext
{

   public PostMatchContainerRequestContext(HttpRequest request, ResourceMethodInvoker resourceMethod)
   {
      super(request, resourceMethod);
   }
}
