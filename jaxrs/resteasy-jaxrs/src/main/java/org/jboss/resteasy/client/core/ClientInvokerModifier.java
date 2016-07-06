package org.jboss.resteasy.client.core;

/**
 * used to modify all of the ClientInvokers of a given ResteasyClientProxy. @see
 * ResteasyClientProxy.applyClientInvokerModifier
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 *             
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
public interface ClientInvokerModifier
{
   void modify(ClientInvoker invoker);
}
