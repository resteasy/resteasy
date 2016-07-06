package org.jboss.resteasy.client.core.marshallers;

import java.util.Collection;
import java.util.Iterator;

import org.jboss.resteasy.client.ClientRequest;

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
public class MatrixParamMarshaller implements Marshaller
{
   private String paramName;

   public MatrixParamMarshaller(String paramName)
   {
      this.paramName = paramName;
   }

   public void build(ClientRequest request, Object object)
   {
      if (object == null) return; // Don't add a null matrix parameter
      if (object instanceof Collection)
      {
         for (Iterator<?> it = Collection.class.cast(object).iterator(); it.hasNext(); )
         {
            request.matrixParameter(paramName, it.next());
         }
      }
      else
      {
         request.matrixParameter(paramName, object);
      }
   }

}