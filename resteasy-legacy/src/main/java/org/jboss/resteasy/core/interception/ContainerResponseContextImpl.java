package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl instead.
 */
@Deprecated
public class ContainerResponseContextImpl extends org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl
{

   public ContainerResponseContextImpl(HttpRequest request, HttpResponse httpResponse, BuiltResponse serverResponse)
   {
      super(request, httpResponse, serverResponse);
   }

}
