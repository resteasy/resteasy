package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

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

   public void buildUri(Object object, UriBuilderImpl uri)
   {
      uri.matrixParam(paramName, object.toString());
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }

}