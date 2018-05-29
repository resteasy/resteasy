package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerModifier;

import java.util.Collection;

/**
 * implemented by every generated proxy
 *
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClient
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public interface ResteasyClientProxy
{
   Collection<ClientInvoker> getResteasyClientInvokers();

   void applyClientInvokerModifier(ClientInvokerModifier modifier);
   
   <T> T as(Class<T> iface);
}
