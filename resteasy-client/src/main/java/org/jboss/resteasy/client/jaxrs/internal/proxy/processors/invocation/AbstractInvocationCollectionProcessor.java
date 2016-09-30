package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.AbstractCollectionProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractInvocationCollectionProcessor extends AbstractCollectionProcessor<ClientInvocation> implements InvocationProcessor
{
   public AbstractInvocationCollectionProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   public void process(ClientInvocation invocation, Object param)
   {
      buildIt(invocation, param);
   }
}
