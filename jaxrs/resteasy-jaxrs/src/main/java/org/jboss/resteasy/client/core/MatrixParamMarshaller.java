package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.ClientRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamMarshaller implements Marshaller
{
   private String paramName;

   public MatrixParamMarshaller(String paramName)
   {
      this.paramName = paramName;
   }

   public void build(ClientRequest request, Object object)
   {
      request.matrixParameter(paramName, object);
   }

}