package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerModifier;

import java.util.Collection;

/**
 * implemented by every generated proxy
 *
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 *  
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
public interface ResteasyClientProxy
{
   Collection<ClientInvoker> getResteasyClientInvokers();

   void applyClientInvokerModifier(ClientInvokerModifier modifier);
   
   <T> T as(Class<T> iface);
}
