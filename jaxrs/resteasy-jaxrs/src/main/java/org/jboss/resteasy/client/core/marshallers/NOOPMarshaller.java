package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;

/**
 * Marshaller that doesn't do anything with the target. Useful for @Context parameters which are server-generated.
 *
 * @author Stephane Epardaud
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
public class NOOPMarshaller implements Marshaller
{

   public void build(ClientRequest request, Object target)
   {
      // do nothing at all
   }

}
