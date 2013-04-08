package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.AbstractCollectionProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractInvocationCollectionProcessor extends AbstractCollectionProcessor<ClientInvocationBuilder> implements InvocationProcessor
{
   public AbstractInvocationCollectionProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   public void process(ClientInvocationBuilder invocation, Object param)
   {
      buildIt(invocation, param);
   }
}
