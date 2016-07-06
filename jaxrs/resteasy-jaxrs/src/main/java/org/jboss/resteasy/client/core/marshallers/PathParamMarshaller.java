package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
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
public class PathParamMarshaller implements Marshaller
{
   private String paramName;
   private boolean encoded;
   private ResteasyProviderFactory factory;

   public PathParamMarshaller(String paramName, boolean encoded, ResteasyProviderFactory factory)
   {
      this.paramName = paramName;
      this.encoded = encoded;
      this.factory = factory;
   }

   public void build(ClientRequest request, Object object)
   {
      request.pathParameter(paramName, object);
   }

}
