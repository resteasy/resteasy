package org.jboss.resteasy.client.core;

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
}
