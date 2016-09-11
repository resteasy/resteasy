package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamProcessor extends AbstractInvocationCollectionProcessor
{

   public HeaderParamProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   protected ClientInvocation apply(ClientInvocation invocation, Object object)
   {
       invocation.getHeaders().header(paramName, object);
       return invocation;
   }

}