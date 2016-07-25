package org.jboss.resteasy.client.jaxrs.internal.proxy;

import java.util.Collection;

/**
 * implemented by every generated proxy
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyClientProxy
{
   Collection<ClientInvoker> getResteasyClientInvokers();

   void applyClientInvokerModifier(ClientInvokerModifier modifier);
   
   <T> T as(Class<T> iface);
}
