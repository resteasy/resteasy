package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ClientHttpOutput;

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

   public void marshall(Object object, UriBuilderImpl uri, ClientHttpOutput output)
   {
      uri.matrixParam(paramName, object.toString());
   }
}