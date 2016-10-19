package org.jboss.resteasy.client.core;

/**
 * used to modify all of the ClientInvokers of a given ResteasyClientProxy. @see
 * ResteasyClientProxy.applyClientInvokerModifier
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public interface ClientInvokerModifier
{
   void modify(ClientInvoker invoker);
}
