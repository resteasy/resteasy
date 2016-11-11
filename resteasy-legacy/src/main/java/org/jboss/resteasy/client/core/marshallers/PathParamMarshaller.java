package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
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
