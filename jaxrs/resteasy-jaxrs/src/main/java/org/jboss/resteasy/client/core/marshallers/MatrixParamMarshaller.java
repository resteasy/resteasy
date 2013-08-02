package org.jboss.resteasy.client.core.marshallers;

import java.util.Collection;
import java.util.Iterator;

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